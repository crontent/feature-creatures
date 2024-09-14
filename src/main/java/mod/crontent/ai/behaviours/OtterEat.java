package mod.crontent.ai.behaviours;

import com.mojang.datafixers.util.Pair;
import mod.crontent.ai.memories.ModMemoryModuleTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class OtterEat <E extends LivingEntity> extends ExtendedBehaviour<E> {

    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(ModMemoryModuleTypes.SHOULD_EAT)
            .noMemory(MemoryModuleType.ATTACK_TARGET);

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean shouldRun(ServerWorld level, E entity) {
        return Boolean.TRUE.equals(BrainUtils.getMemory(entity, ModMemoryModuleTypes.SHOULD_EAT));
    }


    @Override
    protected void start(E entity) {
        entity.dropStack(Items.GHAST_SPAWN_EGG.getDefaultStack());
        entity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        entity.sendMessage(Text.of("I AM EATING"));
    }
}
