package mod.crontent.registries;

import mod.crontent.FeatureCreatures;
import mod.crontent.entities.OtterEntity;
import mod.crontent.util.EntityRegistryManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class ModEntities extends EntityRegistryManager {

    public static final EntityType<OtterEntity> OTTER = register("otter", OtterEntity::new, SpawnGroup.CREATURE, .8f, .55f, 10);

    public static void initialize() {
        FeatureCreatures.LOGGER.info("Registering Entities");

        addAttributes(OTTER, OtterEntity.createOtterAttributes());
    }
}
