package org.jahdoo.all_magic.all_abilities.utility.raw_utilities;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.particle.ParticleHandlers;

import java.util.ArrayList;
import java.util.List;

public class BlockExplosion extends AbstractUtilityProjectile {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("block_bomb_property");

    boolean hasHitBlock;
    int totalRadius;
    int explosionTimer;
    int explosionTimerMax = 50;
    int totalRadiusMax = 20;
    double projectileSphere;
    boolean keepItems = false;
    int itemsDroppedIndex = 0;  // New variable to track the index of the next item to drop
    List<ItemStack> destroyedItems = new ArrayList<>();

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockExplosion();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.hasHitBlock = true;
        GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundEvents.SLIME_BLOCK_PLACE, 1.5f);
        genericProjectile.setDeltaMovement(0, 0, 0);
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (genericProjectile.level() instanceof ServerLevel serverLevel) {
            if (projectileSphere < (double) totalRadiusMax / 8) projectileSphere += 0.05;
            if (explosionTimer < explosionTimerMax) {
                GeneralHelpers.getRandomSphericalPositions(genericProjectile, projectileSphere, projectileSphere * 40,
                    radiusPosition -> {
                        GeneralHelpers.generalHelpers.sendParticles(serverLevel, ElementRegistry.UTILITY.get().getParticleGroup().baked(), radiusPosition.add(0,0.1,0), 1,
                            GeneralHelpers.Random.nextDouble(0.1, 0.2),
                            GeneralHelpers.Random.nextDouble(0.1, 0.2),
                            GeneralHelpers.Random.nextDouble(0.1, 0.2),
                            GeneralHelpers.Random.nextDouble(0.05, 0.1)
                        );
                    }
                );
            } else {
                if (totalRadius <= totalRadiusMax  + 1){
                    GeneralHelpers.getRandomSphericalPositions(genericProjectile, totalRadius - 1, totalRadius * 100,
                        radiusPosition -> {
                            GeneralHelpers.generalHelpers.sendParticles(serverLevel, ElementRegistry.UTILITY.get().getParticleGroup().baked(), radiusPosition.add(0,0.1,0), 1,
                                GeneralHelpers.Random.nextDouble(0.1, 0.2),
                                GeneralHelpers.Random.nextDouble(0.1, 0.2),
                                GeneralHelpers.Random.nextDouble(0.1, 0.2),
                                GeneralHelpers.Random.nextDouble(0.05, 0.1)
                            );
                            GeneralHelpers.generalHelpers.sendParticles(serverLevel, ElementRegistry.UTILITY.get().getParticleGroup().genericSlow(), radiusPosition.add(0,0.1,0), 1,
                                GeneralHelpers.Random.nextDouble(0.1, 0.2),
                                GeneralHelpers.Random.nextDouble(0.1, 0.2),
                                GeneralHelpers.Random.nextDouble(0.1, 0.2),
                                GeneralHelpers.Random.nextDouble(0.05, 0.1)
                            );
                        }
                    );
                }
            }

            if (hasHitBlock) {
                explosionTimer++;
                if (explosionTimer <= explosionTimerMax + totalRadiusMax) {
                    if (explosionTimer % 4 == 0 && !(explosionTimer >= explosionTimerMax)) {
                        GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.TIMER.get(), 1f);
                        ParticleHandlers.spawnPoof(serverLevel, genericProjectile.position().add(0, 0.2, 0), totalRadiusMax, ElementRegistry.UTILITY.get().getParticleGroup().genericSlow(), 0, -0.1, 0, 0.05f);
                        ParticleHandlers.spawnPoof(serverLevel, genericProjectile.position().add(0, 0.2, 0), explosionTimer, ElementRegistry.UTILITY.get().getParticleGroup().baked(), 0, -0.1, 0, 0.01f);
                    }

                    if (explosionTimer >= explosionTimerMax) {
                        GeneralHelpers.generalHelpers.sendParticles(
                            serverLevel, ElementRegistry.UTILITY.get().getParticleGroup().baked(), genericProjectile.position().add(0,0.2,0),
                            20 * totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
                        );
                        GeneralHelpers.generalHelpers.sendParticles(
                            serverLevel, ElementRegistry.UTILITY.get().getParticleGroup().genericSlow(), genericProjectile.position().add(0,0.2,0),
                            20 * totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
                        );

                        GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.EXPLOSION.get(), 2f);
                        handleItemsAndExplosion(serverLevel);
                        if (totalRadius <= totalRadiusMax) totalRadius++;
                    }
                }

            } else {
                genericProjectile.setDeltaMovement(genericProjectile.getDeltaMovement().x, genericProjectile.getDeltaMovement().y - projectileSphere / 50, genericProjectile.getDeltaMovement().z);
                if (genericProjectile.tickCount % 12 == 0) {
                    GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.TIMER.get(), 1f);
                }
            }
            if (genericProjectile.tickCount > 400 && !hasHitBlock) genericProjectile.discard();

            if (explosionTimer >= explosionTimerMax + 20) {
                if (!destroyedItems.isEmpty()) {
                    for (int i = 0; i < 60 && itemsDroppedIndex < destroyedItems.size(); i++) {
                        genericProjectile.spawnAtLocation(destroyedItems.get(itemsDroppedIndex));
                        itemsDroppedIndex++;
                    }
                    ParticleHandlers.spawnPoof(serverLevel, genericProjectile.position().add(0, 0.2, 0), totalRadiusMax, ElementRegistry.UTILITY.get().getParticleGroup().genericSlow(), 0, 0, 0, 0.1f);
                } else {
                    genericProjectile.discard();
                }

                if (itemsDroppedIndex >= destroyedItems.size()) genericProjectile.discard();
            }
        }
    }

    private void handleItemsAndExplosion(ServerLevel serverLevel){
        GeneralHelpers.getSphericalBlockPositions(genericProjectile, totalRadius,
            radiusPosition -> {
                if (UtilityHelpers.range.contains(UtilityHelpers.destroySpeed(radiusPosition, genericProjectile.level()))) {
                    BlockState blockstate = genericProjectile.level().getBlockState(radiusPosition);
                    if(keepItems){
                        LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) genericProjectile.level())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(radiusPosition)).withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
                        this.destroyedItems.addAll(genericProjectile.level().getBlockState(radiusPosition).getDrops(lootparams$builder));
                    }
                    GeneralHelpers.generalHelpers.sendParticles(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, blockstate),  radiusPosition.getCenter(), 1, 0, 0, 0, 0.1);
                    genericProjectile.level().removeBlock(radiusPosition, false);
                }
            }
        );
    }
}
