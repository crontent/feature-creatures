package mod.crontent;

import mod.crontent.entities.OtterEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.Objects;

public class OtterEntityRenderer extends GeoEntityRenderer<OtterEntity> {

    public OtterEntityRenderer(EntityRendererFactory.Context renderManager) {

        super(renderManager, new OtterEntityModel());

        addRenderLayer(new BlockAndItemGeoLayer<>(this)
        {
            @Override
            protected @Nullable ItemStack getStackForBone(GeoBone bone, OtterEntity otter) {
                return Objects.equals(bone.getName(), "head_item") ? otter.getEquippedStack(EquipmentSlot.MAINHAND) : null;
            }


            //TODO: This is shit
            @Override
            protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, OtterEntity animatable, VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
                //Quaternionf rotationX = new Quaternionf(new AxisAngle4f((float) Math.toRadians(90), 1, 0, 0));
                //Quaternionf rotationY = new Quaternionf(new AxisAngle4f((float) Math.toRadians(90), 0, 0, 1));

                //rotationX.mul(rotationY);

                //poseStack.multiply(rotationX);
                //poseStack.translate(0, 0, 2);
                poseStack.scale(.3f, .3f, .3f);

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });


    }
}
