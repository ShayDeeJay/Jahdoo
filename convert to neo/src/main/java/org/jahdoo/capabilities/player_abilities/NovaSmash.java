package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.capabilities.AbstractCapability;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.particle.ParticleStore.rgbToInt;

public class NovaSmash implements AbstractCapability {

    private int highestDelta;
    private boolean canSmash;
    private float getDamage;

    @Override
    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("highestDelta", highestDelta);
        nbt.putBoolean("canSmash", canSmash);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        this.highestDelta = nbt.getInt("highestDelta");
        this.canSmash = nbt.getBoolean("canSmash");
    }

    @Override
    public void copyFrom(AbstractCapability source) {
        if(source instanceof NovaSmash novaSmashAbility){
            this.highestDelta = novaSmashAbility.highestDelta;
            this.canSmash = novaSmashAbility.canSmash;
        }
    }

    public void setCanSmash(boolean canSmash){
        this.canSmash = canSmash;
    }

    public void setHighestDelta(int highestDelta){
        this.highestDelta = highestDelta;
    }

    public boolean getCanSmash(){
        return this.canSmash;
    }

    public int getSetHighestDelta(){
        return this.highestDelta;
    }

    public void setPlayerSmash(Player player){
        int getCurrentDelta = (int) Math.abs(Math.round(player.getDeltaMovement().y));
        this.highestDelta = Math.max(this.highestDelta, getCurrentDelta);

        if (this.canSmash){
            player.setDeltaMovement(player.getDeltaMovement().add(0, -1.5, 0));
            player.resetFallDistance();
            this.clientDataSync(player);
            if(player.onGround()){
                this.setAbilityEffects(player, this.highestDelta);
                this.setKnockbackAndDamage(player, this.highestDelta);
                player.setDeltaMovement(player.getDeltaMovement().add(0, Math.max(Math.min(5, (double) this.highestDelta / 4), 2.2), 0));
                this.highestDelta = 0;
                this.canSmash = false;
            }
        }
    }

    private void setAbilityEffects(Player player, int getMaxDeltaMovement){
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.PLAYER_BIG_FALL);
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.EXPLOSION.get(), 0.5f,1.5f);

        if(player.level() instanceof ServerLevel serverLevel){
            this.clientDiggingParticles(player, serverLevel);
            GeneralHelpers.getOuterRingOfRadiusRandom(player.position().add(0, 0.1, 0), 0.5, Math.max(getMaxDeltaMovement * 40, 20),
                worldPosition -> this.setParticleNova(player, worldPosition, 5, serverLevel)
            );
        }
    }


    public void clientDataSync(Player player){
//        if(player instanceof ServerPlayer serverPlayer){
//            Network.sendToClient(new NovaSmashS2CPacket(this.highestDelta, this.canSmash), serverPlayer);
//        }
    }

    private void setKnockbackAndDamage(Player player, int getMaxDeltaMovement){
        player.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, player,
            player.getBoundingBox().inflate(6, 2, 6)
        ).forEach(
            livingEntity -> {
                double deltaX = livingEntity.getX() - player.getX();
                double deltaY = livingEntity.getY() - player.getY();
                double deltaZ = livingEntity.getZ() - player.getZ();
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if(livingEntity != player){
                    this.knockback(livingEntity, Math.max((double) getMaxDeltaMovement / 2, 0.3), -deltaX / length, -deltaZ / length);
                    livingEntity.hurt(player.damageSources().playerAttack(player), DamageCalculator.getCalculatedDamage(player, getMaxDeltaMovement));
                }
            }
        );
    }

    private void knockback(LivingEntity targetEntity, double pStrength, double pX, double pZ) {

//        LivingKnockBackEvent event = onLivingKnockBack(targetEntity, (float) pStrength, pX, pZ);
//
//        if(event.isCanceled()) return;
//        pStrength = event.getStrength();
//        pX = event.getRatioX();
//        pZ = event.getRatioZ();
//        pStrength *= 1.0D - targetEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
//        if (!(pStrength <= 0.0D)) {
//            targetEntity.hasImpulse = true;
//            Vec3 vec3 = targetEntity.getDeltaMovement();
//            Vec3 vec31 = (new Vec3(pX, 0.0D, pZ)).normalize().scale(pStrength);
//            targetEntity.setDeltaMovement(vec3.x / 2.0D - vec31.x, targetEntity.onGround() ? Math.min(0.8D, vec3.y / 2.0D + pStrength) : vec3.y, vec3.z / 2.0D - vec31.z);
//        }
    }

    public void clientDiggingParticles(LivingEntity livingEntity, ServerLevel serverLevel) {
        RandomSource randomsource = livingEntity.getRandom();
        BlockState blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 45; ++i) {
                double d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
                double d1 = livingEntity.getY();
                double d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
                GeneralHelpers.generalHelpers.sendParticles(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), new Vec3(d0, d1, d2), 2, 0, 0.5,0,0.5);
            }
        }
    }

    private void setParticleNova(Player player, Vec3 worldPosition, double particleMultiplier, ServerLevel serverLevel){
        AbstractElement element = ElementRegistry.MYSTIC.get();

        Vec3 positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 0.1f);
        Vec3 directions = positionScrambler.subtract(player.position()).normalize();
        ParticleOptions genericParticle = genericParticleOptions(
            GENERIC_PARTICLE_SELECTION, 3, GeneralHelpers.Random.nextFloat(1f, 2f),element.particleColourPrimary(),
            rgbToInt(255,255,255), true
        );

        ParticleOptions bakedParticle = new BakedParticleOptions(
            element.getTypeId(),
            (int) (particleMultiplier * 30),
            GeneralHelpers.Random.nextFloat(0.3f, 0.5f),
            true
        );

        List<ParticleOptions> getRandomParticle = List.of(bakedParticle, genericParticle);

        GeneralHelpers.generalHelpers.sendParticles(
            serverLevel, getRandomParticle.get(GeneralHelpers.Random.nextInt(2)) ,worldPosition, 0, directions.x, directions.y, directions.z, GeneralHelpers.Random.nextDouble(0.3,1.0)
        );

    }

}
