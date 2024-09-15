package mod.crontent.registries;

import mod.crontent.FeatureCreatures;
import mod.crontent.entities.OtterEntity;
import mod.crontent.util.ModIdentifier;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModSpawnEggs {

    public static final Item OTTER_SPAWN_EGG = register("otter_spawn_egg", ModEntities.OTTER, 0xFFFFFF, 0x000000);

    public static Item register(String id, EntityType<OtterEntity> entityType, int primaryColor, int secondaryColor){
        return Registry.register(Registries.ITEM, ModIdentifier.of(id), new SpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Settings()));
    }

    public static void init(){
        FeatureCreatures.LOGGER.info("Registering ModSpawnEggs");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS)
                .register((itemGroup) -> itemGroup.add(OTTER_SPAWN_EGG));
    }
}
