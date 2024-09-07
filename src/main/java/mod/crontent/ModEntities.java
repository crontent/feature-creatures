package mod.crontent;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {
    public static final EntityType<OtterEntity> OTTER = register("otter", OtterEntity::new, SpawnGroup.CREATURE, .8f, .55f, 10);

    private static EntityType<OtterEntity> register(String id,
                                                    EntityType.EntityFactory<OtterEntity> factory,
                                                    SpawnGroup spawnGroup,
                                                    float width,
                                                    float height,
                                                    int range) {
        return Registry.register(Registries.ENTITY_TYPE,
                FCUtil.id(id),
                EntityType.Builder.create(factory, spawnGroup)
                        .dimensions(width, height)
                        .maxTrackingRange(range)
                        .build());
    }

    public static void initialize() {
        FeatureCreatures.LOGGER.info("Registering Entities");
    }
}
