package mod.crontent.entities;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.crontent.ai.behaviours.OtterEat;
import mod.crontent.ai.behaviours.SetWalkTargetToEatSpot;
import mod.crontent.ai.behaviours.SetWalkTargetToItem;
import mod.crontent.ai.memories.ModMemoryModuleTypes;
import mod.crontent.ai.sensors.HasFoodInSlotSensor;
import mod.crontent.ai.sensors.NearbyOtterEatSpotSensor;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Panic;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyItemsSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class OtterEntity extends AnimalEntity implements GeoEntity, SmartBrainOwner<OtterEntity>  {
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk_cycle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    static final BiPredicate<ItemEntity, OtterEntity> PICKABLE_ITEMS_FILTER = (item, entity) -> entity.canGather(item.getStack()) && entity.canSee(item);
    static final Predicate<ItemStack> FOOD_ITEM_FILTER = item -> item.isIn(ItemTags.FISHES);
    static final Predicate<ItemStack> PRIORITY_ITEM_FILTER = item -> item.isOf(Items.GRAVEL);

    public OtterEntity(EntityType<? extends OtterEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new OtterMoveControl(this);
        this.setCanPickUpLoot(true);
    }

    //BOILERPLATE
    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }

    //BOILERPLATE
    @Override
    protected void mobTick() {
        tickBrain(this);
    }



    @Override
    public List<? extends ExtendedSensor<? extends OtterEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<OtterEntity>()
                        .setPredicate((target, entity) ->
                                target instanceof PlayerEntity ||
                                        target instanceof IronGolemEntity ||
                                        target instanceof WolfEntity),
                new NearbyItemsSensor<OtterEntity>()
                        .setRadius(5d)
                        .setPredicate((target, entity) -> {
                            if (PICKABLE_ITEMS_FILTER.test(target, entity)) {
                                ItemStack currentStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
                                int priority_current = getPickupPriority(currentStack);
                                int priority_prospective_pickup = getPickupPriority(target.getStack());
                                return priority_prospective_pickup > priority_current;
                            }
                            return false;
                        }),
                new HurtBySensor<>(),
                new NearbyOtterEatSpotSensor<>(),
                new HasFoodInSlotSensor<>()
        );
    }

    private int getPickupPriority(ItemStack stack) {
        int prio = stack.isEmpty() ? 0 : 1;
        if(PRIORITY_ITEM_FILTER.test(stack)) prio = 3;
        else if (FOOD_ITEM_FILTER.test(stack)) prio = 2;
        return prio;
    }

    @Override
    public BrainActivityGroup<OtterEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                //new AvoidEntity<>().avoiding(entity -> entity instanceof PlayerEntity),
                new LookAtTarget<>().runFor(entity -> entity.getRandom().nextBetween(40,300)),
                new MoveToWalkTarget<>(),
                new FloatToSurfaceOfFluid<>()
                        .riseChance(.2f)
                        .startCondition(mobEntity -> mobEntity.getAir() <= 100)
                        .runFor(e -> e.getRandom().nextBetween(80, 200)));

    }

    @Override
    public BrainActivityGroup<OtterEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<OtterEntity>(
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                //Always Panic if applicable
                new FirstApplicableBehaviour<OtterEntity>(
                        new Panic<>(),
                        new OtterEat<>(30),
                        new SetWalkTargetToItem<>()
                                .whenSuccessful(livingEntity -> {
                                    if (getPickupPriority(livingEntity.getEquippedStack(EquipmentSlot.MAINHAND)) != 0) {
                                        ItemStack item = livingEntity.getEquippedStack(EquipmentSlot.MAINHAND);
                                        livingEntity.dropStack(item).setPickupDelay(40);
                                        livingEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                                    }
                                })
                                .cooldownFor(e -> 100),

                        new SetWalkTargetToEatSpot<>()
                                .whenSuccessful(livingEntity -> BrainUtils.setMemory(livingEntity, ModMemoryModuleTypes.SHOULD_EAT, Boolean.TRUE))
                                .startCondition(livingEntity -> Boolean.TRUE.equals(BrainUtils.getMemory(livingEntity, ModMemoryModuleTypes.HAS_FOOD)))
                                .cooldownFor(livingEntity -> 600),

                        //Idling if nothing else is happening
                        new OneRandomBehaviour<>(
                                Pair.of(
                                        new SetRandomWalkTarget<>(),
                                        1),
                                Pair.of(
                                        new Idle<OtterEntity>().runFor(e -> e.getRandom().nextBetween(30, 60)),
                                        5)
                        )
                )
        );
    }

    @Override
    public BrainActivityGroup<OtterEntity> getFightTasks() {
        // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }

    public static DefaultAttributeContainer.Builder createOtterAttributes(){
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 16D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, .4f);
    }

    @Override
    protected void initGoals() {
        //MAKE SURE THIS IS UNSET
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        //this.equipStack(EquipmentSlot.MAINHAND, Items.DIAMOND.getDefaultStack());
    }


    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.initEquipment(random, difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new OtterSwimNavigation(this, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walking", 5, this::walkAnimController));
    }

    protected <E extends OtterEntity> PlayState walkAnimController(final AnimationState<E> event){
        if (event.isMoving()) {
            return event.setAndContinue(WALK_ANIM);
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public void tick() {
        if(!this.getWorld().isClient) {
            System.out.println(BrainUtils.getMemory(this, ModMemoryModuleTypes.SHOULD_EAT));
        }
        super.tick();
    }

    static class OtterMoveControl extends MoveControl{
        private final OtterEntity otter;

        OtterMoveControl(OtterEntity otter) {
            super(otter);
            this.otter = otter;
        }

    }

    static class OtterSwimNavigation extends AmphibiousSwimNavigation {

        public OtterSwimNavigation(MobEntity owner, World world) {
            super(owner, world);
        }


    }

}
