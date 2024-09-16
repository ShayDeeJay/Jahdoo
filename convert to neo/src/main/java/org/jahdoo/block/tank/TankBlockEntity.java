package org.jahdoo.block.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;


public class TankBlockEntity extends AbstractBEInventory {

    int counter;
    private static final int INPUT = 0;

    public TankBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.TANK_BE.get(), pPos, pBlockState, 64);
    }

    public ItemStack getRenderer() {
        return this.inputItemHandler.getStackInSlot(INPUT);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(!(pLevel instanceof ServerLevel serverLevel)) return;
        BlockState blockState = pLevel.getBlockState(pPos.below());


        int tankSlotSize = this.inputItemHandler.getStackInSlot(0).getCount();
        if(!(blockState.is(BlocksRegister.CRYSTAL_ORE.get()))) {
            if(this.counter > 0) this.counter = 0;
            return;
        }

        if(tankSlotSize < 64){
            counter++;
            GeneralHelpers.getOuterRingOfRadiusRandom(pPos.getCenter().subtract(0, 0.5, 0), 0.5, 2,
                positions -> {
                    Vec3 direction = pPos.getCenter().add(0, 1, 0).subtract(positions).normalize();

                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel, processingParticle(5,0.1f, true, 0.1),
                        positions, 0,
                        direction.x, direction.y, direction.z,
                        GeneralHelpers.Random.nextDouble(0.08,0.12)
                    );
                }
            );
        }

        if(counter >= 200){
            if(tankSlotSize <= 64){
                pLevel.destroyBlock(pPos.below(), false);
                this.inputItemHandler.setStackInSlot(0, new ItemStack(ItemsRegister.JIDE_POWDER.get()).copyWithCount(Math.min(6 + tankSlotSize, 64)));
            }
            counter = 0;
        }
    }

    @Override
    public int setInputSlots() {
        return 1;
    }

    @Override
    public int setOutputSlots() {
        return 1;
    }

    @Override
    public int getMaxSlotSize() {
        return 64;
    }


}

