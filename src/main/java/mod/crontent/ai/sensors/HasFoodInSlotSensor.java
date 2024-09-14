package mod.crontent.ai.sensors;

import mod.crontent.ai.memories.ModMemoryModuleTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class HasFoodInSlotSensor<E extends LivingEntity> extends ExtendedSensor<E> {
    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return List.of(ModMemoryModuleTypes.HAS_FOOD);
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensors.HAS_FOOD_IN_SLOT;
    }

    @Override
    protected void sense(ServerWorld level, E entity) {
        if (entity.getEquippedStack(EquipmentSlot.MAINHAND).isIn(ItemTags.FISHES))  BrainUtils.setMemory(entity, ModMemoryModuleTypes.HAS_FOOD, true);
        else BrainUtils.setMemory(entity, ModMemoryModuleTypes.HAS_FOOD, false);
    }
}
