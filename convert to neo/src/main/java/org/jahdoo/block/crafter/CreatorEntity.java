package org.jahdoo.block.crafter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.recipe.CreatorRecipe;
import org.jahdoo.recipe.ModRecipes;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.abilityAttributes.AbstractAttribute;
import org.jahdoo.utils.abilityAttributes.CooldownCalculator;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.abilityAttributes.ManaCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Optional;

import static org.jahdoo.utils.GeneralHelpers.getFormattedFloat;

public class CreatorEntity extends AbstractTankUser {

    public double animationTicker;

    public double animateDistanceIncrement = 0.5f;
    private double animationTickerIncrement = 0.5f;

    public CreatorEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.CREATOR_BE.get(), pPos, pBlockState, 1);
        this.craftingFuelCost = 6;
    }

    public void tick(Level level, BlockPos blockPos, BlockState pState) {
        this.assignTankBlockInRange(level, blockPos);

        if(this.canCraftWand()){
            this.progress++;
            this.tableProcessingParticle();
            this.onCompleteCraft(level, blockPos);
            if(this.progress % 4 == 0){
                this.sendProcessingParticle(-0.2, 0.32, 6, 0.15f, 0.2);
            }
            this.setAnimationTickerIncrement(Math.min(this.animationTickerIncrement + 0.1, 2.5));
        } else {
            this.setAnimationTickerIncrement(Math.max(this.animationTickerIncrement - 0.1, 0.5));
            if(this.progress > 0) this.progress = 0;
        }
    }

    private void tableProcessingParticle(){
        if(!(this.level instanceof ServerLevel serverLevel)) return;

        if(this.progress % 2 == 0){
            GeneralHelpers.getOuterRingOfRadiusRandom(this.getBlockPos().getCenter(), this.animationTickerIncrement / 5, 20,
                worldPosition -> {
                    Vec3 directions = this.getBlockPos()
                        .getCenter()
                        .subtract(worldPosition)
                        .normalize()
                        .offsetRandom(RandomSource.create(), 2f);
                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel,
                        processingParticle(6,0.25f, false, 0.1),
                        worldPosition.add(0, 0.6f, 0), 0, directions.x, directions.y, directions.z, 0.05
                    );
                }
            );
        }

        if(this.progress % 10 == 0){
            GeneralHelpers.getSoundWithPosition(serverLevel, this.getBlockPos(), SoundEvents.BEACON_AMBIENT, 0.5f, 2f);
        }
    }

    public void onCompleteCraft(Level level, BlockPos blockPos){
        if(!this.isCompletedCraft()) return;

        this.chargeTankFuel();
        spawnWand(level, blockPos.above());
        for(int i = 0; i < this.inputItemHandler.getSlots(); i++) this.inputItemHandler.getStackInSlot(i).shrink(1);
        GeneralHelpers.getSoundWithPosition(level, blockPos, SoundEvents.BEACON_POWER_SELECT, 0.5f, 0.8f);
        this.progress = 0;
    }

    public ItemStack getOutputResult(){
        if(getCurrentRecipe(level).isEmpty()) return ItemStack.EMPTY;
        return getCurrentRecipe(level).get().getResultItem(level.registryAccess());
    }

    private void spawnWand(Level level, BlockPos blockPos){
        ItemStack itemStack = this.getOutputResult();
        if(getCurrentRecipe(level).isEmpty()) return;

//        level.setBlock(blockPos, blockItem.getBlock().defaultBlockState(), 2);
//        if(!(level.getBlockEntity(blockPos) instanceof WandBlockEntity wandBlockEntity)) return;

        AbstractAttribute.setAttributeToItem(itemStack, true, getFormattedFloat(GeneralHelpers.Random.nextFloat(0, 20)), DamageCalculator.DAMAGE_MULTIPLIER);
        AbstractAttribute.setAttributeToItem(itemStack, true, getFormattedFloat(GeneralHelpers.Random.nextFloat(0, 5)), ManaCalculator.MANA_REDUCTION);
        AbstractAttribute.setAttributeToItem(itemStack, true, getFormattedFloat(GeneralHelpers.Random.nextFloat(0, 5)), CooldownCalculator.COOLDOWN_REDUCTION);
//        AbstractAttribute.setAttributeToItem(itemStack, false, 400, ManaRegen.MANA_REGENERATION);
//        WandSlotManager.createNewSlotsForWand(itemStack, 3);
        itemStack.set(DataComponentRegistry.AUGMENT_RATING.get(), 19.0);

//        itemStack.getOrCreateTag().putDouble(TagModifierHelper.AUGMENT_RATING, 19);
//        itemStack.getOrCreateTag().putDouble(TagModifierHelper.ABILITY_SLOTS, 5);
//        wandBlockEntity.itemHandler.setStackInSlot(GET_WAND_SLOT, itemStack);
        this.outputItemHandler.setStackInSlot(0, itemStack);
        if(itemStack.getItem() instanceof WandItem){
            this.spawnSuccessfulWandCraftParticles(level, blockPos, itemStack);
        }
    }


    private void spawnSuccessfulWandCraftParticles(Level level, BlockPos blockPos, ItemStack itemStack){
        if(!(level instanceof ServerLevel serverLevel)) return;
        if(itemStack.isEmpty() && !(itemStack.getItem() instanceof WandItem)) return;

        ParticleHandlers.spawnPoof(
            serverLevel, blockPos.getCenter().add(0, 0.5f, 0), 10,
            ParticleHandlers.genericParticleOptions(
                ParticleStore.SOFT_PARTICLE_SELECTION,
                ElementRegistry.getElementByWandType(itemStack.getItem()).get(0), 30, 0.6f
            ),
            0, 0.5, 0, 0.1f
        );
    }

    public boolean isCompletedCraft(){
        return this.progress == 100;
    }


    public double getAnimationTickerIncrement(){
        return this.animationTickerIncrement;
    }

    public void setAnimationTickerIncrement(double animationTickerIncrement){
        this.animationTickerIncrement = animationTickerIncrement;
    }

    public boolean canCraftWand(){
        return getCurrentRecipe(level).isPresent()
            && this.hasTankAndFuel()
            && this.outputItemHandler.getStackInSlot(0).isEmpty();
    }

    private Optional<CreatorRecipe> getCurrentRecipe(Level level) {
        SimpleContainer inventory = new SimpleContainer(this.inputItemHandler.getSlots());
        for(int i = 0; i < inputItemHandler.getSlots(); i++) {
            inventory.setItem(i, inputItemHandler.getStackInSlot(i));
        }


//        return level.getRecipeManager().getAllRecipesFor(CreatorRecipe.Type.INSTANCE.);
        return Optional.empty();
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

    @Override
    public int setInputSlots() {
        return 9;
    }

    @Override
    public int setOutputSlots() {
        return 1;
    }

    @Override
    public int getMaxSlotSize() {
        return 1;
    }
}
