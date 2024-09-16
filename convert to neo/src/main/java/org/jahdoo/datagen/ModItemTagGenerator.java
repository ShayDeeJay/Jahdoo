package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.tags.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {


    public ModItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags,JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Items.WAND_TAGS)
            .add(ItemsRegister.WAND_ITEM_INFERNO.get())
            .add(ItemsRegister.WAND_ITEM_MYSTIC.get())
            .add(ItemsRegister.WAND_ITEM_FROST.get())
            .add(ItemsRegister.WAND_ITEM_LIGHTNING.get())
            .add(ItemsRegister.WAND_ITEM_VITALITY.get());


        this.tag(ModTags.Items.INFUSE_EFFECT)
                .add(Items.CARROT)
                .add(Items.GOLDEN_APPLE)
                .add(Items.GOLDEN_CARROT)
                .add(Items.GLISTERING_MELON_SLICE)
                .add(Items.COOKED_PORKCHOP)
                .add(Items.COOKED_BEEF)
                .add(Items.COOKED_RABBIT)
                .add(Items.APPLE)
                .add(Items.COOKED_SALMON)
                .add(Items.BREAD)
                .add(Items.PUMPKIN_PIE)
                .add(Items.GLOW_BERRIES)
                .add(Items.DRIED_KELP)
                .add(Items.HONEY_BOTTLE)
                .add(Items.COOKIE)
                .add(Items.COOKED_COD)
                .add(Items.PUFFERFISH);
    }
}
