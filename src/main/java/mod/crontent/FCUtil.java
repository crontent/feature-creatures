package mod.crontent;

import net.minecraft.util.Identifier;

public class FCUtil {

    public static Identifier id(String id){
      return Identifier.of(FeatureCreatures.MOD_ID, id);
    }
}
