package mod.crontent.ai.sensors;

import mod.crontent.FeatureCreatures;
import mod.crontent.util.ModIdentifier;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import java.util.function.Supplier;

public class ModSensors{

    public static final SensorType<? extends ExtendedSensor<?>> HAS_FOOD_IN_SLOT = register("has_food_in_slot_sensor", HasFoodInSlotSensor::new);
    public static SensorType<NearbyOtterEatSpotSensor<?>> NEARBY_OTTER_EAT_SPOT = register("nearby_otter_eat_spot_sensor", NearbyOtterEatSpotSensor::new);

    private static <T extends ExtendedSensor<?>> SensorType<T> register(String id, Supplier<T> sensor) {
        return Registry.register(Registries.SENSOR_TYPE, ModIdentifier.of(id), new SensorType<>(sensor));
    }

    public static void init() {
        FeatureCreatures.LOGGER.info("Registering Mod Sensors");
    }
}
