package mod.crontent.ai.behaviours;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class SetWalkTargetToItem<E extends LivingEntity> extends ExtendedBehaviour<E> {

    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(SBLMemoryTypes.NEARBY_ITEMS.get())
            .noMemory(MemoryModuleType.ATTACK_TARGET);

    protected BiPredicate<E, ItemEntity> predicate = (entity, item) -> true;
    protected BiFunction<E, ItemEntity, Float> speedMod = (owner, pos) -> 1f;
    protected BiFunction<E, ItemEntity, Integer> closeEnoughDist = (entity, pos) -> 0;

    protected ItemEntity target = null;
    private Consumer<E> successCallback = entity -> {};

    /**
     * Set the predicate to determine whether a given position/state should be the target path
     * @param predicate The predicate
     * @return this
     */
    public SetWalkTargetToItem<E> predicate(final BiPredicate<E, ItemEntity> predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Set the movespeed modifier for the entity when moving to the target.
     * @param speedModifier The movespeed modifier/multiplier
     * @return this
     */
    public SetWalkTargetToItem<E> speedMod(BiFunction<E, ItemEntity, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    /**
     * Set the distance (in blocks) that is 'close enough' for the entity to be considered at the target position
     * @param function The function
     * @return this
     */
    public SetWalkTargetToItem<E> closeEnoughWhen(final BiFunction<E, ItemEntity, Integer> function) {
        this.closeEnoughDist = function;
        return this;
    }


    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean shouldRun(ServerWorld level, E entity) {
        for (ItemEntity item : BrainUtils.getMemory(entity, SBLMemoryTypes.NEARBY_ITEMS.get())) {
            if (this.predicate.test(entity, item)) {
                this.target = item;
                break;
            }
        }

        return this.target != null;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return true;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.target.getBlockPos(), this.speedMod.apply(entity, this.target), this.closeEnoughDist.apply(entity, this.target)));
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.target, true));
    }

    @Override
    protected void stop(E entity) {
        this.target = null;
    }

    @Override
    protected void tick(E entity) {
        if(this.target != null && entity.getBlockPos().getSquaredDistance(this.target.getBlockPos()) <= 1) {
            this.successCallback.accept(entity);
            this.stop(entity);
        }
    }

    public final ExtendedBehaviour<E> whenSuccessful(Consumer<E> callback) {
        this.successCallback = callback;
        return this;
    }
}
