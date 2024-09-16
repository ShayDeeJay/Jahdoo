package org.jahdoo.all_magic.all_abilities.utility.raw_utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.particle.ParticleHandlers;

public class Hammer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("hammer_property");
    int size = 3;
    BlockHitResult blockHitResult;
    int counter;

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Hammer();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        LivingEntity owner = (LivingEntity) genericProjectile.getOwner();
        if(owner == null) return;

        if(genericProjectile.level() instanceof ServerLevel serverLevel) {
            this.blockHitResult = blockHitResult;
            int[] sizes = {3, 6, 9, 12};
            int radius = (sizes[size] - 1) / 2;
            BlockPos pos = blockHitResult.getBlockPos();
            Direction pDirection = owner.getDirection();
            double lookAngleY = genericProjectile.getLookAngle().y;

            boolean isLookingUpOrDown = lookAngleY < -0.8 || lookAngleY > 0.8;
            boolean axisZ = pDirection.getAxis() == Direction.Axis.Z;
            boolean axisX = pDirection.getAxis() == Direction.Axis.X;

            genericProjectile.level().playSound(
                null, genericProjectile.getX(), genericProjectile.getY(), genericProjectile.getZ(),
                genericProjectile.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound(),
                SoundSource.BLOCKS, 1, 1
            );

            int eyeHeightInt = size > 1 ? size + 1 : size;
            pos = pos.relative(lookAngleY < -0.8 ? pDirection.getOpposite() : pDirection, isLookingUpOrDown ? eyeHeightInt : 0)
                .above(isLookingUpOrDown ? 0 : eyeHeightInt);

            if (UtilityHelpers.range.contains(UtilityHelpers.destroySpeed(blockHitResult.getBlockPos(), genericProjectile.level()))) {
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos offsetPos = pos.offset(
                                x * (isLookingUpOrDown || axisZ ? 1 : 0),
                                y * (isLookingUpOrDown ? 0 : 1),
                                z * (isLookingUpOrDown || axisX ? 1 : 0)
                            );

                            UtilityHelpers.dropItemsOrBlock(genericProjectile, offsetPos, true, true);

                            ParticleHandlers.spawnPoof(serverLevel, offsetPos.getCenter(), 1,
                                ElementRegistry.UTILITY.get().getParticleGroup().genericSlow(),
                                !(isLookingUpOrDown && axisX) ? 0 : 0.15, isLookingUpOrDown ? 0 : 0.15, !(isLookingUpOrDown && axisZ) ? 0 : 0.15,
                                0.005f, 1
                            );
                        }
                    }
                }
            }
            counter++;
            if(counter == 1){
                genericProjectile.discard();
            }
        }
    }

}
