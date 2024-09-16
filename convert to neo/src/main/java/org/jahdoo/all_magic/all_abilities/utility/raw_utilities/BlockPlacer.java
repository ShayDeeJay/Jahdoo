package org.jahdoo.all_magic.all_abilities.utility.raw_utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.utils.GeneralHelpers;

public class BlockPlacer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("block_placer_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockPlacer();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        Player player = (Player) genericProjectile.getOwner();
        if(player == null) return;
        Block replaceBlock = Block.byItem(player.getOffhandItem().getItem());

        BlockPos blockPos = blockHitResult.getBlockPos();
        Direction side = blockHitResult.getDirection();

        if (!player.level().isClientSide) {
            if (player.level().getBlockState(blockPos.relative(side)).canBeReplaced()) {
                if (replaceBlock != Blocks.AIR) {
                    player.level().setBlockAndUpdate(blockPos.relative(side), replaceBlock.defaultBlockState());
                    GeneralHelpers.getSoundWithPosition(genericProjectile.level(), blockPos, replaceBlock.defaultBlockState().getSoundType().getBreakSound());
                    if (!player.isCreative()) {
                        ItemStack handItem = player.getOffhandItem();
                        boolean itemFound = false;
                        for (ItemStack itemStack : player.getInventory().items) {
                            if (itemStack.is(handItem.getItem()) && player.getInventory().selected != player.getInventory().items.indexOf(itemStack)) {
                                itemStack.shrink(1);
                                itemFound = true;
                                break;
                            }
                        }

                        if (!itemFound && player.getInventory().selected != -1) {
                            ItemStack selectedSlotItem = player.getInventory().getItem(player.getInventory().selected);
                            (selectedSlotItem.is(handItem.getItem()) ? selectedSlotItem : handItem).shrink(1);
                        }
                    }
                }
            }
        }
        genericProjectile.discard();
    }
}
