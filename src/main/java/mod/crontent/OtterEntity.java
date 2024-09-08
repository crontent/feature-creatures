package mod.crontent;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class OtterEntity extends AnimalEntity implements GeoEntity {
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk_cycle");

    //private static final TrackedData<Optional<UUID>> TRUSTED =
            //DataTracker.registerData(OtterEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);


    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    static final Predicate<ItemEntity> PICKABLE_ITEMS_FILTER = item -> !item.cannotPickup() && item.isAlive();
    //TODO: create own tag.
    static final Predicate<ItemStack> FOOD_ITEM_FILTER = item -> item.isIn(ItemTags.FOX_FOOD);
    private boolean eatBound;

    public OtterEntity(EntityType<? extends OtterEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new OtterMoveControl(this);
        this.setCanPickUpLoot(true);
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
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeEntityGoal<>(this,
                PlayerEntity.class,
                10.0F,
                1.6,
                1.4,
                EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test));
        this.goalSelector.add(2, new FindSpaceAndEatGoal(this));
        this.goalSelector.add(5, new WanderAroundGoal(this, 0.8D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 5.0F));
        this.goalSelector.add(9, new PickupItemGoal());
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND));
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

    boolean wantsToPickupItem() {
        //TODO: check for states such as already moving towards an item, or searching
        return true;
    }

    public void setEatBound(boolean eatBound) {
        this.eatBound = eatBound;
    }

    public boolean isEatBound() {
        return eatBound;
    }

    static class OtterMoveControl extends MoveControl{
        private final OtterEntity otter;

        OtterMoveControl(OtterEntity otter) {
            super(otter);
            this.otter = otter;
        }

        private void updateVelocity(){

        }

        @Override
        public void tick() {
            super.tick();
        }
    }

    static class OtterSwimNavigation extends AmphibiousSwimNavigation {

        public OtterSwimNavigation(MobEntity owner, World world) {
            super(owner, world);
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            return super.isValidPosition(pos);
        }
    }

    class PickupItemGoal extends Goal {
        public PickupItemGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            OtterEntity otter = OtterEntity.this;
            if (!otter.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()){
                return false;
            } else if (otter.getTarget() != null || otter.getAttacker() != null){
                return false;
            } else if (!otter.wantsToPickupItem()){
                return false;
            } else if (otter.getRandom().nextInt(toGoalTicks(10)) != 0 ){
                return false;
            } else{
                List<ItemEntity> itemsClose = getCloseItemEntities(otter);
                return !itemsClose.isEmpty() && otter.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
            }
        }

        @Override
        public void tick() {
            OtterEntity otter = OtterEntity.this;
            List<ItemEntity> itemsClose = getCloseItemEntities(otter);
            ItemStack equippedStack = otter.getEquippedStack(EquipmentSlot.MAINHAND);
            if(equippedStack.isEmpty() && !itemsClose.isEmpty()){
                otter.getNavigation().startMovingTo(itemsClose.getFirst(), 1.2f);
            }
        }

        private static List<ItemEntity> getCloseItemEntities(OtterEntity otter) {
            return otter.getWorld()
                    .getEntitiesByClass(ItemEntity.class,
                            otter.getBoundingBox().expand(8d, 8d, 8d),
                            OtterEntity.PICKABLE_ITEMS_FILTER);
        }

        @Override
        public void start() {
            OtterEntity otter = OtterEntity.this;
            List<ItemEntity> itemsClose = getCloseItemEntities(otter);
            if(!itemsClose.isEmpty()){
                otter.getNavigation().startMovingTo(itemsClose.getFirst(), 1.2f);
            }
        }
    }

    private class FindSpaceAndEatGoal extends Goal {
        private BlockPos targetPos;
        private int eatTimer;
        private OtterEntity otter;
        private boolean noPath;
        private int reachFoodLocationTryTicks;

        public FindSpaceAndEatGoal(OtterEntity otter) {
            this.otter = otter;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            ItemStack heldItem = otter.getEquippedStack(EquipmentSlot.MAINHAND);
            if (heldItem.isEmpty() ){ //|| !isNextToWater(otter.getBlockPos())) {
                return false;
            } else if (otter.getRandom().nextInt(1) != 0){
                this.targetPos = findNearbyWaterBlock();
                return this.targetPos != null;
            } else return false;
        }


        @Override
        public void start() {
            otter.setEatBound(true);
            this.noPath = false;
            this.reachFoodLocationTryTicks = 0;
        }

        @Override
        public void stop() {
            otter.setEatBound(false);
        }

        @Override
        public boolean shouldContinue() {
            return this.targetPos.isWithinDistance(otter.getPos(), 2d) &&
                    !this.noPath
                    && this.reachFoodLocationTryTicks < this.getTickCount(600);
        }

        @Override
        public void tick() {
            if (otter.getBlockPos().isWithinDistance(targetPos, 1f)){
                this.eatTimer++;
                if (this.eatTimer >= 40) {
                    otter.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    otter.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1f, 1f);
                }
            }
        }

        private BlockPos findNearbyWaterBlock() {
            BlockPos pos = otter.getBlockPos();
            for(BlockPos offset : BlockPos.iterate(pos.add(-3,-1,3), pos.add(3,1,3))) {
                if(isNextToWater(offset)) {
                    FeatureCreatures.LOGGER.warn("Found block to eat at {}", offset);
                    return offset;
                }
            }
            return null;
        }

        private boolean isNextToWater(BlockPos pos) {
            return OtterEntity.this.getWorld().getBlockState(pos.north()).getFluidState().isIn(FluidTags.WATER)
                    || OtterEntity.this.getWorld().getBlockState(pos.south()).getFluidState().isIn(FluidTags.WATER)
                    || OtterEntity.this.getWorld().getBlockState(pos.east()).getFluidState().isIn(FluidTags.WATER)
                    || OtterEntity.this.getWorld().getBlockState(pos.west()).getFluidState().isIn(FluidTags.WATER);
        }

        @Override
        public boolean canStop() {
            return false;
        }
    }
}
