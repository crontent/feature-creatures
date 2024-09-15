package mod.crontent.datagen;

import mod.crontent.registries.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelGen extends FabricModelProvider {
    public ModModelGen(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.PEBBLES, Models.GENERATED);
        itemModelGenerator.register(ModItems.DIAMOND_PEBBLES, Models.GENERATED);
        itemModelGenerator.register(ModItems.OTTER_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.OTTER_MEAT_COOKED, Models.GENERATED);
    }
}
