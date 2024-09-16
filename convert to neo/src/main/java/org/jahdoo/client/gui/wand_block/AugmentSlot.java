package org.jahdoo.client.gui.wand_block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class AugmentSlot extends SlotItemHandler {

    WandBlockMenu wandBlockMenu;

    public AugmentSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        WandBlockMenu wandBlockMenu
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.wandBlockMenu = wandBlockMenu;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return itemStack.is(ItemsRegister.AUGMENT_ITEM.get()) &&
            itemStack.getComponents().has(DataComponentRegistry.WAND_ABILITY_HOLDER.get()) &&
            doesWandHaveAbility(itemStack.get(DataComponentRegistry.GET_ABILITY_KEY.get())) ||
            canSwapCarried();

    }



    public boolean doesWandHaveAbility(String abilityLocation){
        ItemStack wandItem = wandBlockMenu.getWandBlockEntity().getWandItemFromSlot();
        Map<String, AbilityHolder> abilityHolderMap = wandItem
            .get(DataComponentRegistry.WAND_ABILITY_HOLDER.get())
            .abilityProperties();
        return !abilityHolderMap.containsKey(abilityLocation);
    }

    public boolean canSwapCarried(){
        if(this.wandBlockMenu.getCarried().is(ItemsRegister.AUGMENT_ITEM.get())){
            String storedItemLocationID = this.getItem().get(DataComponentRegistry.GET_ABILITY_KEY.get());
            String carriedItemLocationID = wandBlockMenu.getCarried().get(DataComponentRegistry.GET_ABILITY_KEY.get());
            return Objects.equals(storedItemLocationID, carriedItemLocationID) ;
        }
        return false;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        if(!this.hasItem()) return;
        ItemStack wandItem = this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getStackInSlot(0);
        String ability = pStack.get(DataComponentRegistry.GET_ABILITY_KEY.get());
        String wandAbility = wandItem.get(DataComponentRegistry.GET_ABILITY_KEY.get());

        if(Objects.equals(ability, wandAbility)){
            ItemStack copiedStack = wandItem.copy();
            copiedStack.set(DataComponentRegistry.GET_ABILITY_KEY.get(), "");
            this.wandBlockMenu.getWandBlockEntity().inputItemHandler.setStackInSlot(0, copiedStack);
        }

        wandItem.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()).abilityProperties().remove(ability);
    }

    @Override
    public void setChanged() {
        this.wandBlockMenu.getWandBlockEntity().setAllAbilities();
        if(this.wandBlockMenu.getCarried().isEmpty() || this.wandBlockMenu.getCarried().getItem() instanceof Augment){
            GeneralHelpers.getSoundWithPosition(
                Objects.requireNonNull(this.wandBlockMenu.getWandBlockEntity().getLevel()),
                this.wandBlockMenu.getWandBlockEntity().getBlockPos(),
                SoundEvents.ARMOR_EQUIP_CHAIN.value()
            );
        }
    }

    @Override
    public boolean isHighlightable() {
        return false;
    }
}