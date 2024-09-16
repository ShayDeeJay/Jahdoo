package org.jahdoo.utils.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.GeneralHelpers;

import java.util.Map;
import java.util.Optional;

public class TagHelper {

    public static class TagKeys {
        //Get current ability from wand or tome
        public static final String GET_ABILITY_KEY = "jahdoo_ability_type";
        // Store an array of ints that correspond to the stored ability_entities;
        public static final String WAND_STORED_ABILITIES = "wand_stored_abilities";
    }

    public static Optional<AbilityHolder> getWandAbilities(ItemStack itemStack){
        return Optional.ofNullable(itemStack.get(DataComponentRegistry.ABILITY_HOLDER.get()));
    }

    public static double getSpecificValue(Player player, ItemStack itemStack, String modifier){
        ResourceLocation abilityName = TagHelper.getAbilityTypeWand(player);
        var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        var allModifiers = wandAbilityHolder.abilityProperties().get(abilityName.getPath().intern());
        var specificValue = allModifiers.abilityProperties().get(modifier);
        return specificValue.actualValue();
    }

    public static double getSpecificValue(String name, WandAbilityHolder wandAbilityHolder, String modifier){
        var allModifiers = wandAbilityHolder.abilityProperties().get(name);
        var specificValue = allModifiers.abilityProperties().get(modifier);
        return specificValue.actualValue();
    }

    public static Map<String, AbilityHolder.AbilityModifiers> getSpecificValue(Player player){
        ResourceLocation abilityName = TagHelper.getAbilityTypeWand(player);
        var wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        var allModifiers = wandAbilityHolder.abilityProperties().get(abilityName.getPath().intern());
        var specificValue = allModifiers.abilityProperties();
        return specificValue;
    }



    public static Optional<AbilityHolder.AbilityModifiers> getSpecificAbility(String ability, Player player){
        Optional<AbilityHolder> wandAbilityHolder = getWandAbilities(player.getMainHandItem());
        return wandAbilityHolder.flatMap(
            abilityHolder -> Optional.ofNullable(abilityHolder.abilityProperties().get(ability))
        );
    }

    public static boolean hasWandAbilitiesTag(ItemStack itemStack){
        return itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()) != null;
    }

    public static Optional<AbilityHolder.AbilityModifiers> setSpecificAbility(String ability, ItemStack itemStack, AbilityHolder.AbilityModifiers abilityModifiers){
        Optional<AbilityHolder> wandAbilityHolder = getWandAbilities(itemStack);
        return wandAbilityHolder.flatMap(
            abilityHolder -> Optional.ofNullable(abilityHolder.abilityProperties().put(ability, abilityModifiers))
        );
    }

//    public static CompoundTag getWandAbilitiesItemStack(String abilityId, ItemStack itemStack){
//        return itemStack.getOrCreateTag().getCompound(WAND_STORED_ABILITIES).getCompound(abilityId);
//    }

    public static ResourceLocation getAbilityTypeWand(Player player) {
        if(player != null && player.getMainHandItem().getItem() instanceof WandItem){
            ItemStack playerWand = player.getMainHandItem();
            String abilityName = playerWand.get(DataComponentRegistry.GET_ABILITY_KEY.get());
            return GeneralHelpers.modResourceLocation(abilityName);
        }
        return null;
    }

    public static void setAbilityTypeWand(Player player, String ability) {
        if(player != null && player.getMainHandItem().getItem() instanceof WandItem){
            ItemStack wandItem = player.getMainHandItem();
            wandItem.set(DataComponentRegistry.GET_ABILITY_KEY.get(), ability);
        }
    }

    public static void setAbilityTypeItemStack(ItemStack itemStack, String ability) {
        itemStack.set(DataComponentRegistry.GET_ABILITY_KEY.get(), ability);
    }

    public static String getAbilityTypeItemStack(ItemStack itemStack) {
//        return itemStack.getOrCreateTag().getString(GET_ABILITY_KEY);
        return itemStack.get(DataComponentRegistry.GET_ABILITY_KEY.get());
    }

    public static ResourceLocation getAbilityTypeItemStackResource(ItemStack itemStack) {
//        return new ResourceLocation(JahdooMod.MOD_ID, itemStack.getOrCreateTag().getString(GET_ABILITY_KEY));
        String name = itemStack.get(DataComponentRegistry.GET_ABILITY_KEY.get());
//        return GeneralHelpers.modResourceLocation(name);
        return ResourceLocation.withDefaultNamespace("");
    }
}
