package org.jahdoo.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jahdoo.utils.GeneralHelpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatorRecipe implements Recipe<CraftingInput> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;

    public CreatorRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public boolean matches(CraftingInput pInput, Level pLevel) {
        List<String> recipeItems = new ArrayList<>();
        List<String> inventoryItem = new ArrayList<>();

        for(int i = 0; i < inputItems.size(); i++){
            List<ItemStack> small = List.of(inputItems.get(i).getItems());
            for(ItemStack itemStack : small){
                recipeItems.add(itemStack.getDescriptionId());
            }
            inventoryItem.add((pInput.getItem(i)).getDescriptionId());
        }

        Collections.sort(recipeItems);
        Collections.sort(inventoryItem);

        return recipeItems.equals(inventoryItem);
    }

    @Override
    public ItemStack assemble(CraftingInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public String getRecipeID(){
        return id.getPath().intern();
    }

    public static class Type implements RecipeType<CreatorRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "creator_block";
    }

    public static class Serializer implements RecipeSerializer<CreatorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = GeneralHelpers.modResourceLocation("creator_block");

        public static final MapCodec<CreatorRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Ingredient.CODEC_NONEMPTY
                    .listOf()
                    .fieldOf("ingredients")
                    .flatXmap(
                        ingredients -> {
                            Ingredient[] aingredient = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                            return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                        },
                        DataResult::success
                    )
                    .forGetter(creatorRecipe -> creatorRecipe.inputItems),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),   // Resulting item
                ResourceLocation.CODEC.fieldOf("id").forGetter(creatorRecipe -> creatorRecipe.id)
            ).apply(instance, CreatorRecipe::new)  // Combine fields to create a ShapedRecipe
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CreatorRecipe> STREAM_CODEC = StreamCodec.of(
            Serializer::toNetwork,  // Function for writing to the network buffer
            Serializer::fromNetwork  // Function for reading from the network buffer
        );

        private static CreatorRecipe fromNetwork(RegistryFriendlyByteBuf byteBuf) {
            int i = byteBuf.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(byteBuf));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(byteBuf);
            ResourceLocation pRecipeId = ResourceLocation.STREAM_CODEC.decode(byteBuf);
            return new CreatorRecipe(nonnulllist, itemstack, pRecipeId);
        }

        private static void toNetwork(RegistryFriendlyByteBuf byteBuf, CreatorRecipe creatorRecipe) {
            byteBuf.writeResourceLocation(creatorRecipe.id);
            byteBuf.writeVarInt(creatorRecipe.inputItems.size());

            for (Ingredient ingredient : creatorRecipe.inputItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(byteBuf, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(byteBuf, creatorRecipe.output);
        }

        @Override
        public MapCodec<CreatorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CreatorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
