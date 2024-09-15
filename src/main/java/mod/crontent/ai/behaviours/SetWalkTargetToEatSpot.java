package mod.crontent.ai.behaviours;

import com.mojang.datafixers.util.Pair;
import mod.crontent.FeatureCreatures;
import mod.crontent.ai.memories.ModMemoryModuleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class SetWalkTargetToEatSpot<E extends LivingEntity> extends ExtendedBehaviour<E> {

    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(ModMemoryModuleTypes.NEARBY_OTTER_EAT_SPOTS)
            .noMemory(MemoryModuleType.ATTACK_TARGET);


    protected BiPredicate<E, BlockPos> predicate = (entity, item) -> true;
    protected BiFunction<E, BlockPos, Float> speedMod = (owner, pos) -> 1f;
    protected BiFunction<E, BlockPos, Integer> closeEnoughDist = (entity, pos) -> 0;

    protected BlockPos target = null;

    private Consumer<E> successCallback = entity -> {};


    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    /**
     * Set the predicate to determine whether a given position/state should be the target path
     * @param predicate The predicate
     * @return this
     */
    public SetWalkTargetToEatSpot<E> predicate(final BiPredicate<E, BlockPos> predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Set the movespeed modifier for the entity when moving to the target.
     * @param speedModifier The movespeed modifier/multiplier
     * @return this
     */
    public SetWalkTargetToEatSpot<E> speedMod(BiFunction<E, BlockPos, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    /**
     * Set the distance (in blocks) that is 'close enough' for the entity to be considered at the target position
     * @param function The function
     * @return this
     */
    public SetWalkTargetToEatSpot<E> closeEnoughWhen(final BiFunction<E, BlockPos, Integer> function) {
        this.closeEnoughDist = function;
        return this;
    }


    @Override
    protected boolean shouldRun(ServerWorld level, E entity) {
        System.out.println("In Bahaviour: " + BrainUtils.getMemory(entity, ModMemoryModuleTypes.NEARBY_OTTER_EAT_SPOTS));
        for (BlockPos pos : BrainUtils.getMemory(entity, ModMemoryModuleTypes.NEARBY_OTTER_EAT_SPOTS)) {
            level.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
            if (this.predicate.test(entity, pos)) {
                //TODO: choose randomly
                this.target = pos;
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
        FeatureCreatures.LOGGER.warn("Setting Memeries");
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.target, this.speedMod.apply(entity, this.target), this.closeEnoughDist.apply(entity, this.target)));
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.target));
    }

    @Override
    protected void stop(E entity) {
        this.target = null;
    }


    @Override
    protected void tick(E entity) {
        if(this.target != null && entity.getBlockPos().getSquaredDistance(this.target) <= 2) {
            this.successCallback.accept(entity);
            this.stop(entity);
        }
    }

    public final ExtendedBehaviour<E> whenSuccessful(Consumer<E> callback) {
        this.successCallback = callback;
        return this;
    }
}
