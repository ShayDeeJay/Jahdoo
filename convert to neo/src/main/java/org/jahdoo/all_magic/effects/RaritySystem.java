package org.jahdoo.all_magic.effects;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jahdoo.utils.GeneralHelpers;

import java.util.Map;
import java.util.Objects;

public class RaritySystem {

    public static class CrystalRarity {

        public static final String UTILITY_GEM = "Utility Gem";
        public static final String MYTHICAL = "Mythical";
        public static final String RARE = "Rare";
        public static final String UNCOMMON = "Uncommon";
        public static final String COMMON = "Common";


        public String setRarity() {
            float random = GeneralHelpers.Random.nextInt(100);
            String result;
            if (random <= 1) {
                result = MYTHICAL;
            } else if (random <= 5) {
                result = RARE;
            } else if (random <= 40) {
                result = UNCOMMON;
            } else {
                result = COMMON;
            }
            return result;
        }

        public  RarityInformation rarityProperties(String rarity) {
            Map<String, RarityInformation> effectMap = Map.ofEntries(
                Map.entry(MYTHICAL, new RarityInformation(7200, 1, ChatFormatting.GOLD, " II")),
                Map.entry(RARE,  new RarityInformation(3600, 0, ChatFormatting.DARK_PURPLE, " I")),
                Map.entry(UNCOMMON, new RarityInformation(1800, 0, ChatFormatting.DARK_GREEN, " I")),
                Map.entry(COMMON, new RarityInformation(400, 0, ChatFormatting.AQUA, " I"))
            );
            return effectMap.get(rarity) ;
        }

        public record RarityInformation(
           int duration,
           int amplifier,
           ChatFormatting textColour,
           String numeral
        ){}

        public Component rarityDisplay(String rarity) {
            if(!rarity.isEmpty()) {
                return Component.literal("Rarity:" + " ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(rarity)
                    .withStyle(rarityProperties(rarity).textColour));
            }
            return Component.literal("");
        }

        public Component typeDisplay(String type) {
            ChatFormatting colour;
            if(Objects.equals(type, UTILITY_GEM)) {
                colour = ChatFormatting.YELLOW;
            } else {
                colour = ChatFormatting.LIGHT_PURPLE;
            }
            return Component.literal("Type:" + " ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(type)
                .withStyle(colour));
        }

        public  Component getAmplifier(String rarity) {
            return Component.literal(rarityProperties(rarity).numeral).withStyle(ChatFormatting.GRAY);
        }

        public String formatTime(String rarity) {
            int totalSeconds = rarityProperties(rarity).duration / 20;

            int minutes = (totalSeconds % 3600) / 60;
            int seconds = Math.round((float) (totalSeconds % 60));

            return String.format(" (%02d:%02d)", minutes, seconds);
        }

    }
}
