package org.jahdoo.all_magic;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.utils.tags.ModTags;

public class SharedFireProperties {
    public static void fireTrailVegetationRemover(BlockState blockState, BlockPos blockPos, Entity entity, LivingEntity owner){
        if (blockState.is(BlockTags.LEAVES) || blockState.is(ModTags.Block.CAN_REPLACE_BLOCK)) {
            if (!blockState.isAir()) {
                entity.level().removeBlock(blockPos, false);
            }
        }
    }
}
