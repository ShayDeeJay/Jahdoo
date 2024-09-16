package org.jahdoo.block.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.client.gui.wand_block.WandBlockMenu;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.tags.TagHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;
import static org.jahdoo.utils.tags.TagModifierHelper.SET_ELEMENT_TYPE;

public class WandBlockEntity extends AbstractBEInventory implements MenuProvider, GeoBlockEntity {

    int tickCounter;
    public static final int GET_WAND_SLOT = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_block");

    public WandBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.WAND_BE.get(), pPos, pBlockState, 1);
    }


    public void updateView(){
        for(int i = 1; i < this.inputItemHandler.getSlots(); i++) {
            this.inputItemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        WandAbilityHolder storedAbilities = this.inputItemHandler.getStackInSlot(GET_WAND_SLOT)
            .get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());

        AtomicInteger integer = new AtomicInteger(1);

        storedAbilities.abilityProperties().forEach(
            (key, value) -> {
                List<AbstractAbility> abstractAbility = AbilityRegister.getSpellsByTypeId(key);
                if(!abstractAbility.isEmpty()){
                    ItemStack itemStack = new ItemStack(ItemsRegister.AUGMENT_ITEM.get());
                    AbilityHolder abilityHolder = storedAbilities.abilityProperties().get(key);
//                    TagHelper.setAbilityTypeItemStack(itemStack, key);
                    setAbilityToAugment(itemStack, abstractAbility.get(0), storedAbilities);

                    WandAbilityHolder newWAH = new WandAbilityHolder(new HashMap<>());
                    newWAH.abilityProperties().put(key, abilityHolder);
                    itemStack.set(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), newWAH);
                    this.inputItemHandler.setStackInSlot(integer.get(), itemStack);

                } else {
                    this.inputItemHandler.setStackInSlot(integer.get(), ItemStack.EMPTY);
                }
                integer.set(integer.get() + 1);
            }
        );
    }

    public void setAllAbilities(){
        System.out.println(level);
        WandAbilityHolder wandAbilityHolder = new WandAbilityHolder(new LinkedHashMap<>());
        ItemStack wandItem = this.getWandItemFromSlot().copy();

        for(int i = 0; i < this.getAllowedSlots(); i++){
            ItemStack augmentItem = this.inputItemHandler.getStackInSlot(i+1);
            boolean hasAbility = augmentItem.has(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            if(hasAbility){
                String getKeyFromAugment = augmentItem.get(DataComponentRegistry.GET_ABILITY_KEY.get());
                AbilityHolder abilityHolder = augmentItem.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()).abilityProperties().get(getKeyFromAugment);;
                wandAbilityHolder.abilityProperties().put(getKeyFromAugment, abilityHolder);
            } else {
                wandAbilityHolder.abilityProperties().put("empty" + i, new AbilityHolder(Collections.emptyMap()));
            }
        }

        wandItem.set(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), wandAbilityHolder);
        this.inputItemHandler.setStackInSlot(0, wandItem);
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        if(!level.isClientSide){
            Item getWandItem = inputItemHandler.getStackInSlot(GET_WAND_SLOT).getItem();
            List<AbstractElement> getType = getElementByWandType(getWandItem);
            if (getType.isEmpty()) return;
            tickCounter++;

            if (getType.get(0) != null) {
                this.playIdleParticleType(this.level, blockPos, getType.get(0));
            }
        }
    }

    public void playIdleParticleType(Level level, BlockPos blockPos, AbstractElement getType){
        double difference = GeneralHelpers.Random.nextDouble(0.3,0.4);
        if (!(level instanceof ServerLevel serverLevel)) return;

        if(tickCounter % 4 == 0){
            GeneralHelpers.getInnerRingOfRadiusRandom(blockPos, 0.2, 2,
                positions -> {
                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel,
                        getType.getParticleGroup().bakedSlow(),
                        positions.subtract(0,difference,0),
                        1, 0,
                        GeneralHelpers.Random.nextDouble(0.0, 0.02),
                        0, 0.01
                    );

                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel,
                        getType.getParticleGroup().genericSlow(),
                        positions.subtract(0,difference,0),
                        1, 0, 0,
                        GeneralHelpers.Random.nextDouble(0.0, 0.02),
                        0.01
                    );
                }
            );
        }
    }

    public ItemStack getWandItemFromSlot(){
        return this.inputItemHandler.getStackInSlot(0);
    }

    public int slotsWithoutWand(){
        return this.setInputSlots() -1;
    }

    public int getAllowedSlots(){
        Integer getSlots = getWandItemFromSlot().get(DataComponentRegistry.ABILITY_SLOTS.get());
        if(getSlots != null){
            return Math.min(getSlots, slotsWithoutWand());
        }
        return 4;
    }

    @Override
    public void dropsAllInventory(Level level) {
        SimpleContainer inputInventory = new SimpleContainer(setInputSlots());
        inputInventory.setItem(0, this.getWandItemFromSlot());
        Containers.dropContents(level, this.worldPosition, inputInventory);
    }

    @Override
    public int setInputSlots() {
        return 11;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSize() {
        return 1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, state -> state.setAndContinue(IDLE))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.jahdoo.infusion_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WandBlockMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}

