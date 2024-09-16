package org.jahdoo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.networking.packet.SelectedAbilityC2SPacket;
import org.jahdoo.networking.packet.StopUsingC2SPacket;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.tags.ModTags;

import java.util.ArrayList;
import java.util.List;

public class WandAbilitySelector {

    public static void selectWandSlot(int keyNum){
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack playerHandItem = player.getMainHandItem();
        if(!playerHandItem.is(ModTags.Items.WAND_TAGS)) return;

        List<String> arrangedAbilities = new ArrayList<>();
//        CompoundTag compoundTag = playerHandItem.getOrCreateTag().getCompound(WAND_STORED_ABILITIES);

        CompoundTag compoundTag = new CompoundTag();

        int maxIndex = 0;
        for (String key : compoundTag.getAllKeys()) {
            int index = compoundTag.getCompound(key).getInt("index");
            if (index > maxIndex) {
                maxIndex = index;
            }
        }

        for (int i = 0; i <= maxIndex; i++) {
            arrangedAbilities.add(null);
        }

        for (String key : compoundTag.getAllKeys()) {
            int index = compoundTag.getCompound(key).getInt("index") - 1;
            arrangedAbilities.set(index, key);
        }

        boolean condition1 = keyNum < arrangedAbilities.size();
        boolean condition2 = !arrangedAbilities.isEmpty() && arrangedAbilities.get(keyNum - 1) != null;

        if(condition1 || condition2){
            List<AbstractAbility> getAbility = AbilityRegister.getSpellsByTypeId(arrangedAbilities.get(keyNum - 1));
            if(!getAbility.isEmpty()){
                var a1 = getAbility.get(0);
                int colour;

                if (a1.isMultiType()) {
                    int type = compoundTag.getCompound(arrangedAbilities.get(keyNum - 1)).getInt("Element Type");
                    colour = ElementRegistry.getElementByTypeId(type).get(0).textColourSecondary();
                } else {
                    colour = a1.getElemenType().textColourSecondary();
                }

                var a = Component.literal(a1.getAbilityName()).withStyle(style -> style.withColor(colour));
                player.displayClientMessage(a, true);
                PacketDistributor.sendToServer(new StopUsingC2SPacket());
                PacketDistributor.sendToServer(new SelectedAbilityC2SPacket(arrangedAbilities.get(keyNum - 1)));

            } else displayUnassignedKeyMessage(player, keyNum);
        }  else displayUnassignedKeyMessage(player, keyNum);
    }


    private static void displayUnassignedKeyMessage(Player player, int keyNum){
        var a = Component.literal("No ability assigned to slot ").withStyle(style -> style.withColor(-1772304));
        var b = Component.literal(String.valueOf(keyNum)).withStyle(style -> style.withColor(-13457271));
        var c = a.append(b);
        player.displayClientMessage(c, true);
    }
}
