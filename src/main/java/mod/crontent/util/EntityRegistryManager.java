package mod.crontent.util;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntityRegistryManager {

    protected static <T extends Entity> EntityType<T> register(String id,
                                                               EntityType.EntityFactory<T> factory,
                                                               SpawnGroup spawnGroup,
                                                               float width,
                                                               float height,
                                                               int range) {
        return Registry.register(Registries.ENTITY_TYPE,
                ModIdentifier.of(id),
                EntityType.Builder.create(factory, spawnGroup)
                        .dimensions(width, height)
                        .maxTrackingRange(range)
                        .build());
    }

    protected static void addAttributes(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer.Builder builder) {
        FabricDefaultAttributeRegistry.register(entityType, builder);
    }

}
