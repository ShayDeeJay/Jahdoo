package org.jahdoo.items.wand;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static org.jahdoo.items.wand.WandItemHelper.withStyleComponent;

public class WandSlotManager {

    public static final String WAND_SLOT = "new_wand_slot";
    public static final String WAND_SLOT_HOLDER = "jahdoo_wand_slot_holder";


    public static void createNewSlotsForWand(ItemStack itemStack, int slotCount){
        CompoundTag newSlot = new CompoundTag();
        for(int i = 0; i < slotCount; i++){
            newSlot.put(WAND_SLOT + i, new CompoundTag());
        }
//        itemStack.getOrCreateTag().put(WAND_SLOT_HOLDER, newSlot);
    }

    public static int getTotalSlots(ItemStack itemStack){
//        CompoundTag compoundTag = itemStack.getOrCreateTag().getCompound(WAND_SLOT_HOLDER);
//        return compoundTag.getAllKeys().size();
        return 1;
    }

    public static void getSlot(ItemStack itemStack, List<Component> appendComponents){
//        CompoundTag allWandSlots = itemStack.getOrCreateTag().getCompound(WAND_SLOT_HOLDER);
        int colourFaded = -9013642;
//        allWandSlots.getAllKeys().forEach(
//            slots -> {
//                CompoundTag getSlot = allWandSlots.getCompound(slots);
//                String item = getSlot.isEmpty() ? "empty slot" : "new thingy";
//                appendComponents.add(withStyleComponent("☐ " + item, colourFaded));
//            }
//        );
    }

}
