package mod.crontent.ai.sensors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.crontent.ai.memories.ModMemoryModuleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

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

    }
}
