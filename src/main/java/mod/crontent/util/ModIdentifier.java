package mod.crontent.util;

import mod.crontent.FeatureCreatures;
import net.minecraft.util.Identifier;

public class ModIdentifier {

    public static Identifier of(String id){
      return Identifier.of(FeatureCreatures.MOD_ID, id);
    }
}
