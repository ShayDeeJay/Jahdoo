package org.jahdoo.all_magic.all_abilities.utility.raw_utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;

public class BoneMeal extends AbstractUtilityProjectile {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("bone_meal_property");
    boolean hasHitBlock;
    double counter = 0.05;

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BoneMeal();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.hasHitBlock = true;
        this.genericProjectile.setInvisible(true);
        this.genericProjectile.setDeltaMovement(0,0,0);
    }

    public void applyBoneMeal(Level level, BlockPos pPos) {
        BlockState blockstate = level.getBlockState(pPos);
        if (blockstate.getBlock() instanceof BonemealableBlock bonemealableblock) {
            if (bonemealableblock.isValidBonemealTarget(level, pPos, blockstate)) {
                if (level instanceof ServerLevel serverLevel) {
                    if (bonemealableblock.isBonemealSuccess(level, level.random, pPos, blockstate)) {
                        bonemealableblock.performBonemeal(serverLevel, level.random, pPos, blockstate);
                    }
                }
            }
        }
    }

    void fireballNova(Projectile projectile, double novaMaxSize){
        if(counter < novaMaxSize){
            if(counter < 2) counter *= 1.8; else counter += 0.5;
            double particle1 = counter == novaMaxSize ? 0.4 : 0.1  ;

            if(projectile.level() instanceof ServerLevel serverLevel){
                GeneralHelpers.getOuterRingOfRadius(projectile.position(), counter + 1, counter * 30,
                    positions -> {
                        double vx1 = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.5;
                        GeneralHelpers.generalHelpers.sendParticles(
                            serverLevel,
                            genericParticleOptions(this.getElementType(), 4, 1.5f),
                            positions.add(0, 0.3, 0), 1, vx1, vx1, vx1, particle1
                        );
                    }
                );

                GeneralHelpers.getOuterRingOfRadius(projectile.position(), counter, counter,
                    positions -> {
                        this.applyBoneMeal(serverLevel, BlockPos.containing(positions));
                        this.applyBoneMeal(serverLevel, BlockPos.containing(positions).below());
                        GeneralHelpers.getSoundWithPosition(projectile.level(), BlockPos.containing(positions), SoundEvents.BONE_MEAL_USE);
                    }
                );
            }
        } else {
            projectile.discard();
        }
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (!(genericProjectile.level() instanceof ServerLevel)) return;
        if(this.hasHitBlock) this.fireballNova(genericProjectile, 10);
    }


}
