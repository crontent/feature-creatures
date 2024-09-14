package mod.crontent.ai.sensors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.crontent.ai.memories.ModMemoryModuleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.ArrayList;
import java.util.List;

public class NearbyOtterEatSpotSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ModMemoryModuleTypes.NEARBY_OTTER_EAT_SPOTS);

    public NearbyOtterEatSpotSensor() {
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensors.NEARBY_OTTER_EAT_SPOT;
    }

    @Override
    protected void sense(ServerWorld world, E entity) {
        BlockPos entityPos = entity.getBlockPos();

        List<BlockPos> applicable = new ArrayList<>();

        for(BlockPos offset : BlockPos.iterate(entityPos.add(-3,-1,-3), entityPos.add(3,1,3))) {
            if(isNextToWater(world, offset)) {
                applicable.add(offset.toImmutable());
            }
        }


        BrainUtils.setMemory(entity, ModMemoryModuleTypes.NEARBY_OTTER_EAT_SPOTS, applicable);

    }

    private boolean isNextToWater(ServerWorld world, BlockPos pos) {
        return !world.getBlockState(pos).getFluidState().isIn(FluidTags.WATER)
                &&
                //TODO: CHECK ALL WALKTHROUGHABLE BLOCKS
                ( world.getBlockState(pos.up()).isAir() || world.getBlockState(pos.up()).isIn(BlockTags.FLOWERS))
                &&
                (world.getBlockState(pos.north()).getFluidState().isIn(FluidTags.WATER)
                || world.getBlockState(pos.south()).getFluidState().isIn(FluidTags.WATER)
                || world.getBlockState(pos.west()).getFluidState().isIn(FluidTags.WATER)
                || world.getBlockState(pos.east()).getFluidState().isIn(FluidTags.WATER));
    }
}
