package mod.crontent;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OtterEntityRenderer extends GeoEntityRenderer<OtterEntity> {

    public OtterEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OtterEntityModel());
    }
}
