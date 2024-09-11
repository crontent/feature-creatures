package mod.crontent;

import mod.crontent.ai.memories.ModMemoryModuleTypes;
import mod.crontent.ai.sensors.ModSensors;
import mod.crontent.registries.ModSpawnEggs;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureCreatures implements ModInitializer {
	public static final String MOD_ID = "feature-creatures";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModEntities.initialize();
		ModSensors.init();
		ModMemoryModuleTypes.init();

		ModSpawnEggs.init();

		FabricDefaultAttributeRegistry.register(ModEntities.OTTER, OtterEntity.createOtterAttributes());
	}
}