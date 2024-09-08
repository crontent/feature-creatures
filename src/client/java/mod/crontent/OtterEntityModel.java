package mod.crontent;

import mod.crontent.util.ModIdentifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OtterEntityModel extends DefaultedEntityGeoModel<OtterEntity> {
    public OtterEntityModel() {
        super(ModIdentifier.of("otter"), true);
    }
}
