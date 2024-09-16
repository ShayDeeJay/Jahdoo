package org.jahdoo.block.wandBlockManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.gui.infusion_table.InfusionTableMenu;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WandManagerTableEntity extends AbstractTankUser implements MenuProvider {

    public double animationTicker;
    public double animateDistances;
    public double animateDistanceIncrement = 0.5f;
    private double animationTickerIncrement = 0.5f;
    private int privateTicks;

    public WandManagerTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.WAND_MANAGER_TABLE_BE.get(), pPos, pBlockState, 1);
    }


    public void setAnimator(double increment){
        this.animationTicker += increment;
    }


    public void setAnimatedDistance(){
        if(this.canCraftWand()){
            if(this.animateDistanceIncrement < 2.5) this.animateDistanceIncrement += 0.025;
        } else {
            if(this.animateDistanceIncrement > 0.5) this.animateDistanceIncrement -= 0.025;
        }
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {


        if(canCraftWand()){
            this.privateTicks++;
            if(level instanceof ServerLevel serverLevel) {
                if(this.privateTicks % 2 == 0){
                    this.tableProcessingParticle(serverLevel, blockPos);
                }

                if(this.privateTicks % 10 == 0){
                    GeneralHelpers.getSoundWithPosition(serverLevel, blockPos, SoundEvents.BEACON_AMBIENT, 0.5f, 2f);
                }
            }

            this.onCompleteCraft(level, blockPos);
            if(this.animationTickerIncrement < 2.5) this.setAnimationTickerIncrement(this.animationTickerIncrement + 0.1);
        } else {
            if(this.animationTickerIncrement > 0.5) this.setAnimationTickerIncrement(this.animationTickerIncrement - 0.1);
            if(this.privateTicks > 0) this.privateTicks = 0;
        }


    }

    private void tableProcessingParticle(ServerLevel serverLevel, BlockPos pPos){
        GeneralHelpers.getOuterRingOfRadiusRandom(pPos.getCenter(), this.animationTickerIncrement / 5, 20,
            worldPosition -> {
                Vec3 directions = pPos.getCenter().subtract(worldPosition).normalize().offsetRandom(RandomSource.create(), 2f);
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    ParticleHandlers.genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, ElementRegistry.UTILITY.get(), 6, 0.25f),
                    worldPosition.add(0, 0.6f, 0), 0, directions.x, directions.y, directions.z, 0.05
                );
            }
        );
    }


    private void onCompleteCraft(Level level, BlockPos blockPos){
        if(this.isCompletedCraft()){
            for(int i = 0; i < this.inputItemHandler.getSlots(); i++) {
                ItemStack currentItem = this.inputItemHandler.getStackInSlot(i);
                currentItem.shrink(1);
            }
            GeneralHelpers.getSoundWithPosition(level, blockPos, SoundEvents.BEACON_POWER_SELECT, 0.5f, 0.8f);
            spawnWand(level, blockPos.above());
            this.privateTicks = 0;
        }
    }


    private void spawnWand(Level level, BlockPos blockPos){
        BlockState wandBlock = BlocksRegister.WAND.get().defaultBlockState();
        ItemStack itemStack = new ItemStack(ItemsRegister.WAND_ITEM_MYSTIC.get());
        level.setBlock(blockPos, wandBlock, 2);
        if(level.getBlockEntity(blockPos) instanceof WandBlockEntity wandBlockEntity){
            wandBlockEntity.inputItemHandler.setStackInSlot(0, itemStack);
            this.spawnSuccessfulWandCraftParticles(level, blockPos, itemStack);
        }
    }

    private void spawnSuccessfulWandCraftParticles(Level level, BlockPos blockPos, ItemStack itemStack){
        if(level instanceof ServerLevel serverLevel){
            if(!itemStack.isEmpty() && itemStack.getItem() instanceof WandItem){
                ParticleHandlers.spawnPoof(
                    serverLevel, blockPos.getCenter().add(0, 1f, 0), 10,
                    ParticleHandlers.genericParticleOptions(
                        ParticleStore.SOFT_PARTICLE_SELECTION,
                        ElementRegistry.getElementByWandType(itemStack.getItem()).get(0), 30, 0.6f
                    ),
                    0, 0.5, 0,
                    0.1f
                );
            }
        }
    }

    private boolean isCompletedCraft(){
        return this.privateTicks == 100;
    }

    @Override
    public int setInputSlots() {
        return 5;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSize() {
        return 0;
    }

    public double getAnimationTickerIncrement(){
        return this.animationTickerIncrement;
    }

    public void setAnimationTickerIncrement(double animationTickerIncrement){
        this.animationTickerIncrement = animationTickerIncrement;
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("block.jahdoo.infusion_table");
    }

    public boolean canCraftWand(){
        List<Item> getAllIngredients = List.of(
            Items.DIAMOND,
            Items.SPRUCE_LOG,
            ItemsRegister.AUGMENT_CORE.get(),
            Items.PURPLE_DYE
        );

        var mutableCopy = new ArrayList<>(getAllIngredients);

        for(int i = 0; i < this.inputItemHandler.getSlots(); i++) {
            ItemStack currentItem = this.inputItemHandler.getStackInSlot(i);
            mutableCopy.removeIf(item -> item == currentItem.getItem());
        }

        return mutableCopy.isEmpty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new InfusionTableMenu(pContainerId, pPlayerInventory, this, this.data);
    }

}

