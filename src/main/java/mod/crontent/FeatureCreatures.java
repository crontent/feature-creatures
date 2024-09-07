package mod.crontent;

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

		FabricDefaultAttributeRegistry.register(ModEntities.OTTER, OtterEntity.createOtterAttributes());
	}
}