package mod.crontent.ai.behaviours;

import com.mojang.datafixers.util.Pair;
import mod.crontent.ai.memories.ModMemoryModuleTypes;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class OtterEat <E extends LivingEntity> extends DelayedBehaviour<E> {

    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(ModMemoryModuleTypes.SHOULD_EAT)
            .noMemory(MemoryModuleType.ATTACK_TARGET);

    public OtterEat(int delayTicks) {
        super(delayTicks);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean shouldRun(ServerWorld level, E entity) {
        return Boolean.TRUE.equals(BrainUtils.getMemory(entity, ModMemoryModuleTypes.SHOULD_EAT));
    }


    @Override
    protected void tick(E entity) {
        if (entity.getRandom().nextFloat() < 0.75F && !entity.getWorld().isClient && entity.isAlive() && entity.canMoveVoluntarily()) {
            ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
            entity.playSound(entity.getEatSound(itemStack), 1.0F, 1.0F);
            entity.getWorld().sendEntityStatus(entity, EntityStatuses.CREATE_EATING_PARTICLES);
        }

        super.tick(entity);

    }

    @Override
    protected void doDelayedAction(E entity) {
        ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack itemStack2 = itemStack.finishUsing(entity.getWorld(), entity);
        entity.equipStack(EquipmentSlot.MAINHAND, itemStack2);

        BrainUtils.clearMemory(entity, ModMemoryModuleTypes.SHOULD_EAT);


        super.doDelayedAction(entity);
    }
}
