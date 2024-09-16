package org.jahdoo.items.augments;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;

import static org.jahdoo.items.augments.AugmentItemHelper.getModifierContext;

public class AugmentRatingSystem {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public static Component hoverTextHelper(ItemStack itemStack, String keys, String abilityLocation, boolean isHigherBetter) {
//        if (itemStack.getTag() != null) {
//            ListTag listTag = itemStack.getTag().getCompound(abilityLocation).getList(keys, Tag.TAG_DOUBLE);
//            String current = listTag.isEmpty() ? "" : FORMAT.format(listTag.getDouble(0));
//            String max = listTag.isEmpty() ? "" : FORMAT.format((listTag.getDouble(1)));
//            String min = listTag.isEmpty() ? "" : FORMAT.format((listTag.getDouble(2)));
//            return Component.literal(getModifierContext(keys, current, 1).getString() + "/" + getModifierContext(keys, isHigherBetter ? max : min, 1).getString());
//
//        }
        return Component.empty();
    }

    public static Component additionalInformation(ItemStack itemStack, String keys, String abilityLocation, boolean isHigherBetter){
        return Component.literal(" (")
            .append(hoverTextHelper(itemStack, keys, abilityLocation, isHigherBetter))
            .append(")")
            .withStyle(ChatFormatting.DARK_GRAY);
    }

    public static double convertToPercentage(int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("x must be greater than 0");
        }
        // Percentage chance of getting 0 in a range of 0 to x
        return Double.parseDouble(FORMAT.format((1.0 / max) * 100));
    }

    public static int calculateRating(ListTag listTag) {

        boolean higherIsBetter = listTag.getDouble(3) == 1;

        double value = listTag.getDouble(0);
        double minValue = listTag.getDouble(2);
        double maxValue = listTag.getDouble(1);

        double range = maxValue - minValue;
        double relativeValue = value - minValue;

        double normalizedValue;

        if (higherIsBetter) {
            normalizedValue = relativeValue / range;
        } else {
            normalizedValue = 1 - (relativeValue / range);
        }

        normalizedValue = Math.max(0, Math.min(1, normalizedValue));
        return (int)(normalizedValue * 4) + 1; // Map to 1-5 rating
    }

    public static Component displayRating(ItemStack itemStack, String keys, String abilityLocation) {
        StringBuilder string = new StringBuilder();
        int chatFormatting;
        boolean isHigherBetter = true;

//        if(itemStack.getTag() != null){
//            ListTag listTag = itemStack.getTag().getCompound(abilityLocation).getList(keys, Tag.TAG_DOUBLE);
//            int getRating = calculateRating(listTag);
//            isHigherBetter = listTag.getDouble(3) == 1;
//
//            string.append("■".repeat(Math.max(1, getRating)));
//
//            switch (getRating){
//                case 1 -> chatFormatting = -1441791;
//                case 2 -> chatFormatting = -354012;
//                case 3 -> chatFormatting = -79581;
//                case 4 -> chatFormatting = -7155377;
//                default -> chatFormatting= -16752597;
//            }
//        } else {
//            chatFormatting = -1;
//        }

//        return Component.literal(String.valueOf(string))
//            .withStyle(style -> style.withColor(chatFormatting))
//            .append(additionalInformation(itemStack, keys, abilityLocation, isHigherBetter));

        return Component.empty();
    }

}
