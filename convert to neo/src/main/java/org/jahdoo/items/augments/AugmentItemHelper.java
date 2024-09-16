package org.jahdoo.items.augments;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.tags.TagHelper;

import java.util.List;
import java.util.Map;

import static org.jahdoo.items.augments.AugmentRatingSystem.*;
import static org.jahdoo.utils.tags.TagModifierHelper.*;

public class AugmentItemHelper {

    public static InteractionResultHolder<ItemStack> handleStackedAugments(
        ItemStack itemStack,
        Player player,
        InteractionHand interactionHand
    ){

        if(itemStack.getCount() > 1) {
            ItemStack newItem = itemStack.copyWithCount(1);
            itemStack.shrink(1);
            augmentIdentifierShared(newItem, player);
            throwOrAddItem(player, newItem);
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(newItem);
        } else {
            augmentIdentifierShared(itemStack, player);
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemStack);
        }

    }

    public static void setDiscoveryTheme(ServerLevel serverLevel, Player player){
        GeneralHelpers.generalHelpers.sendParticles(
            serverLevel,
            ParticleTypes.TOTEM_OF_UNDYING,
            player.position().add(0, player.getBbHeight()/2, 0),
            50, 0,0.8,0,0.5
        );
        GeneralHelpers.getSoundWithPosition(serverLevel, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,0.7f,0.8F);
        GeneralHelpers.getSoundWithPosition(serverLevel, player.blockPosition(), SoundEvents.BEACON_ACTIVATE,0.5f,2F);
    }

    static void augmentIdentifierShared(ItemStack itemStack, Player player){
        List<AbstractAbility> abstractAbilities = AbilityRegister.REGISTRY.stream().toList();
        AbstractAbility ability = abstractAbilities.get(GeneralHelpers.Random.nextInt(0, abstractAbilities.size()));

        ability.setModifiers(itemStack, player);
        WandAbilityHolder wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());

        setAbilityToAugment(itemStack, ability, wandAbilityHolder);
    }

    public static void setAbilityToAugment(ItemStack itemStack, AbstractAbility ability, WandAbilityHolder wandAbilityHolder){
        TagHelper.setAbilityTypeItemStack(itemStack, ability.setAbilityId());
        if (ability.isMultiType()) {
            AbilityHolder.AbilityModifiers abilityModifiers = wandAbilityHolder
                .abilityProperties()
                .get(ability.setAbilityId())
                .abilityProperties()
                .get(SET_ELEMENT_TYPE);
            CustomModelData customModelData = new CustomModelData((int) abilityModifiers.actualValue());
            itemStack.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
        } else {
            CustomModelData customModelData = new CustomModelData(ability.getElemenType().getTypeId());
            itemStack.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
        }
    }

    public static void throwOrAddItem(Player player, ItemStack newItem){
        boolean isValidSlot = player.getInventory().getFreeSlot() != -1;
        if(isValidSlot) player.addItem(newItem); else throwNewItem(player, newItem);
    }

    public static void throwNewItem(LivingEntity livingEntity, ItemStack itemStack){
        double offsetX = -Math.sin(Math.toRadians(livingEntity.yRotO)) * 2;
        double offsetZ = Math.cos(Math.toRadians(livingEntity.yRotO)) * 2;
        double spawnX = livingEntity.getX() + offsetX;
        double spawnY = livingEntity.getY() + livingEntity.getEyeHeight() -0.7 ; // No vertical offset
        double spawnZ = livingEntity.getZ() + offsetZ;
        BehaviorUtils.throwItem(livingEntity, itemStack, new Vec3(spawnX, spawnY, spawnZ));
    }

    public static void toolTipBase(
        List<Component> toolTips,
        ItemStack itemStack,
        ItemStack itemStack1,
        String keys,
        String abilityLocation,
        int colour,
        boolean addStyle
    ){
        Component component = getCurrentModifierRating2Type(itemStack, itemStack1, keys, abilityLocation);
        Component component1 = getCurrentModifierRating(itemStack, keys, abilityLocation);
        Component getCorrecComponent = itemStack1 == null ? component1 : component;
        Component componentWithStyle = getCorrecComponent.copy().withStyle(style -> style.withColor(colour));

        toolTips.add(addStyle ? componentWithStyle : getCorrecComponent);

        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)) {
            toolTips.add(displayRating(itemStack, keys, abilityLocation));
        }
    }


    public static Component getModifierContext(String keys, String current, int getComparison){
        String converter;

        if(keys.contains("Duration") || keys.contains("Speed") || keys.contains("Delay") || keys.contains("Time")){
            converter = Double.parseDouble(current) / 20 + "s";
        } else if (keys.contains("Chance")) {
            converter = convertToPercentage(Integer.parseInt(current)) + "%";
        } else if (keys.contains("Radius") || keys.contains("Distance") || keys.contains("Range")) {
            converter = current + " Blocks";
        } else if (keys.contains("Multiplier")) {
            converter = current + "x";
        }else {
            converter = current;
        }

        int matchesStat = 5987164;
        int betterThanStat = -12988840;
        int worseThanStat = -47032;

        return Component
            .literal(converter)
            .withStyle(style -> style.withColor( getComparison == 1 ? matchesStat : getComparison == 2 ? worseThanStat : betterThanStat));
    }

    public static Component getCurrentModifierRating(ItemStack itemStack, String keys, String abilityLocation) {
        var empty = Component.empty();
        if (!itemStack.getComponents().has(DataComponentRegistry.WAND_ABILITY_HOLDER.get())) return empty;
        WandAbilityHolder wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        if(wandAbilityHolder == null) return empty;

        Map<String, AbilityHolder> abilityHolder = wandAbilityHolder.abilityProperties();
        CustomModelData customModelData = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(customModelData == null) return empty;

        List<AbstractElement> colour = ElementRegistry.getElementByTypeId(customModelData.value());
        String current = FORMAT.format(abilityHolder.get(abilityLocation).abilityProperties().get(keys).actualValue());

        return Component.literal(keys)
            .withStyle(style -> style.withColor(colour != null ? colour.get(0).particleColourFaded() : -1))
            .append(Component.literal(" | ")
                .withStyle(ChatFormatting.GRAY)
                .append(getModifierContext(keys, current, 1))
            );
    }

    public static Component getCurrentModifierRating2Type(ItemStack itemStack, ItemStack itemStack1, String keys, String abilityLocation) {
        if (!itemStack.has(DataComponentRegistry.GET_ABILITY_KEY.get()) || itemStack1 == null)
            return Component.empty();

        int comparisonResult;
        var matchedTag = itemStack1.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        var hoveredTag = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());

        if(hoveredTag != null && matchedTag != null){
            var getHoveredHolder = hoveredTag.abilityProperties().get(abilityLocation);
            var getMatchedHolder = matchedTag.abilityProperties().get(abilityLocation);
            var type = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
            var colour = ElementRegistry.getElementByTypeId(type.value());
            var abilityModifier = getHoveredHolder.abilityProperties().get(keys);
            var matchedModifier = getMatchedHolder.abilityProperties().get(keys);
            var getMatchedEntry = matchedModifier.actualValue();
            var getHoveredEntry = abilityModifier.actualValue();
            var isHigherBetter = abilityModifier.isHigherBetter();

            var isEven = getHoveredEntry == getMatchedEntry;
            var isBetter = getHoveredEntry > getMatchedEntry;
            var isWorse = getHoveredEntry < getMatchedEntry;

            int higherNumber = isBetter ? 2 : isEven ? 1: 3;
            int lowerNumber = isWorse ? 2 : isEven ? 1: 3;

            comparisonResult = isHigherBetter ? higherNumber : lowerNumber;

            String format = FORMAT.format(abilityModifier.actualValue());

            if(!colour.isEmpty()){
                return Component.literal(keys)
                    .withStyle(style -> style.withColor(colour.get(0).particleColourFaded()))
                    .append(Component.literal(" | ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(getModifierContext(keys, format, comparisonResult)));
            }
        }

        return Component.empty();
    }

    public static void getAllAbilityModifiers(
        ItemStack itemStack,
        ItemStack itemStack1,
        List<Component> toolTips,
        String abilityLocation
    ){
        if(itemStack.getComponents().isEmpty()) return;
        List<String> exceptions = List.of(COOLDOWN, MANA_COST, SET_ELEMENT_TYPE, "index");
        WandAbilityHolder wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        AbilityHolder abilityHolder = wandAbilityHolder.abilityProperties().get(abilityLocation);

        int subHeaderColour = -2434342;
        String attribute = "Core Attributes";
        String unique = "Unique Properties";

        toolTips.add(Component.literal(attribute).withStyle(style -> style.withColor( subHeaderColour)));

        if(abilityHolder == null) return;

        List<String> filteredSuffix = abilityHolder.abilityProperties().keySet()
            .stream()
            .filter(abilityModifiers -> !exceptions.contains(abilityModifiers))
            .toList();

        toolTipBase(toolTips, itemStack, itemStack1, MANA_COST, abilityLocation, -6829330, true);
        toolTipBase(toolTips, itemStack, itemStack1, COOLDOWN, abilityLocation, -7471171, true);

        if(!filteredSuffix.isEmpty()){
            toolTips.add(Component.literal(" "));
            toolTips.add(Component.literal(unique).withStyle(style -> style.withColor(subHeaderColour)));
            filteredSuffix.forEach(keys -> toolTipBase(toolTips, itemStack, itemStack1, keys, abilityLocation, 0, false));
        }
    }

    public static void shiftForDetails(List<Component> toolTips){
        if(!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)){
            Component componentI = Component.literal("[i]").withStyle(style -> style.withColor( -2631721));
            toolTips.add(Component.literal(" "));

            String prefix = "Hold ";
            String suffix = " for details";

            MutableComponent message = Component.literal(prefix)
                .withStyle(ChatFormatting.GRAY)
                .append(componentI)
                .append(Component.literal(suffix).withStyle(ChatFormatting.GRAY));

            toolTips.add(message);
        }
    }

    public static void getHoverText(ItemStack itemStack, List<Component> toolTips){
        if(itemStack.getComponents().has(DataComponentRegistry.WAND_ABILITY_HOLDER.get())){
            WandAbilityHolder wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            String abilityLocation = wandAbilityHolder.abilityProperties().keySet().stream().findAny().get();

            toolTips.add(Component.empty());
            getAllAbilityModifiers(itemStack, null, toolTips, abilityLocation);
            shiftForDetails(toolTips);
        }

        toolTips.add(Component.literal("Right-click to discover").withStyle(ChatFormatting.GRAY));
    }

}
