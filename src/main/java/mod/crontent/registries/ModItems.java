package mod.crontent.registries;

import mod.crontent.FeatureCreatures;
import mod.crontent.util.ModIdentifier;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static Item PEBBLES = register("pebbles", new Item(new Item.Settings()));
    public static Item DIAMOND_PEBBLES = register("diamond_pebbles", new Item(new Item.Settings()));

    public static Item OTTER_MEAT = register("otter_meat", new Item(new Item.Settings()));
    public static Item OTTER_MEAT_COOKED = register("otter_meat_cooked", new Item(new Item.Settings()));


    public static Item register(String id, Item entry){
        return Registry.register(Registries.ITEM,
                ModIdentifier.of(id),
                entry);
    }

    public static void initialize() {
        FeatureCreatures.LOGGER.info("Registering generic items");


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            itemGroup.add(PEBBLES);
            itemGroup.add(DIAMOND_PEBBLES);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((itemGroup) -> {
           itemGroup.add(OTTER_MEAT);
           itemGroup.add(OTTER_MEAT_COOKED);
        });
    }
}
