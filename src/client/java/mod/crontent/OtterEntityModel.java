package mod.crontent;

import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OtterEntityModel extends DefaultedEntityGeoModel<OtterEntity> {
    public OtterEntityModel() {
        super(FCUtil.id("otter"), true);
    }
}
