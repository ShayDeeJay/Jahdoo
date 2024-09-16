package org.jahdoo.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;

public class ModItemModelProvider extends ItemModelProvider {

    private static final ResourceLocation modelData = GeneralHelpers.modResourceLocation("custom_model_data");

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JahdooMod.MOD_ID, existingFileHelper);
    }

    public ModelFile modelFile(String location) {
        return new ModelFile.ExistingModelFile(GeneralHelpers.modResourceLocation(location),this.existingFileHelper);
    }

    @Override
    protected void registerModels() {
        List<String> augmentFiles = List.of(
            "fire_augment",
            "ice_augment",
            "lightning_augment",
            "mystic_augment",
            "vitalis_augment",
            "utility_augment"
        );

        augmentFiles.forEach(this::simpleAugmentItemModel);
        simpleItemOther(ItemsRegister.JIDE_POWDER);
        simpleItemOther(ItemsRegister.HEALTH_CONTAINER);
        simpleItemOther(ItemsRegister.AUGMENT_CORE);

        augmentFiles.forEach( overrider ->
            simpleAugmentItem(ItemsRegister.AUGMENT_ITEM)
                .override()
                .predicate(modelData, augmentFiles.indexOf(overrider) + 1)
                .model(modelFile("item/"+overrider)).end()
        );
    }

    private ItemModelBuilder getWithParent(DeferredHolder<Item, Item> item, String path){
        return withExistingParent(
            item.getId().getPath(),
            ResourceLocation.parse("item/generated")
        ).texture("layer0", GeneralHelpers.modResourceLocation(path));
    }

    private ItemModelBuilder simpleItemOther(DeferredHolder<Item, Item> item) {
        return getWithParent(item, "item/" + item.getId().getPath());
    }

    private ItemModelBuilder simpleAugmentItemModel(String item) {
        return withExistingParent(item,
            ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
            GeneralHelpers.modResourceLocation("item/augments/" + item));
    }

    private ItemModelBuilder simpleAugmentItem(DeferredHolder<Item, Item> item) {
       return getWithParent(item, "item/augments/" + item.getId().getPath());
    }

}
