package mod.crontent.ai.memories;

import com.mojang.serialization.Codec;
import mod.crontent.FeatureCreatures;
import mod.crontent.util.ModIdentifier;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class ModMemoryModuleTypes {

    public static MemoryModuleType<List<BlockPos>> NEARBY_OTTER_EAT_SPOTS = register("nearby_otter_eat_spots");
    public static MemoryModuleType<Boolean> HAS_FOOD = register("has_food");
    public static MemoryModuleType<Boolean> SHOULD_EAT = register("should_eat");

    private static <T> MemoryModuleType<T> register(String id) {
        return register(id, Optional.empty());
    }

    private static <T> MemoryModuleType<T> register(String id, Optional<Codec<T>> codec) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, ModIdentifier.of(id), new MemoryModuleType<>(codec));
    }

    public static void init() {
        FeatureCreatures.LOGGER.info("Registering Mod Memory");
    }
}
