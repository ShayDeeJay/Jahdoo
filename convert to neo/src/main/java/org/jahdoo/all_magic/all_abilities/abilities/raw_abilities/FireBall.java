package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.SharedFireProperties;
import org.jahdoo.all_magic.all_abilities.abilities.FireballAbility;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.rgbToInt;
import static org.jahdoo.utils.tags.TagModifierHelper.*;

public class FireBall extends DefaultEntityBehaviour {
    private boolean hasHitLocation;
    private boolean isBuddy;
    private double aoe = 0.3;
    private double fireballTrail;
    private double maxRadius;
    public List<UUID> hitTargets = new ArrayList<>();

    double damage;
    double effectChance;
    double effectStrength;
    double effectDuration;
    double novaMaxSize;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
//        this.isBuddy = this.elementProjectile.getCompoundTag().getBoolean("isBuddy");
        this.effectChance = this.getTag().getModifierValue(EFFECT_CHANCE);
        this.effectStrength = this.getTag().getModifierValue(EFFECT_STRENGTH);
        this.effectDuration = this.getTag().getModifierValue(EFFECT_DURATION);
        this.novaMaxSize = this.getTag().getModifierValue(FireballAbility.novaRange);

        System.out.println(this.getTag().getModifierValue(FireballAbility.novaRange));

//        System.out.println(novaMaxSize);

        if(this.elementProjectile.getOwner() != null || this.isBuddy){
            this.damage = this.getTag().getModifierValue(DAMAGE);
        } else {
            this.damage = DamageCalculator.getCalculatedDamage((Player) this.elementProjectile.getOwner(), this.getTag().getModifierValue(DAMAGE));
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putBoolean("hasHitLocation", hasHitLocation);
        compoundTag.putDouble("aoe", aoe);
        compoundTag.putDouble("fireballTrail", fireballTrail);
        compoundTag.putDouble("maxRadius", maxRadius);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(FireballAbility.novaRange, this.novaMaxSize);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.hasHitLocation = compoundTag.getBoolean("hasHitLocation");
        this.aoe = compoundTag.getDouble("aoe");
        this.fireballTrail = compoundTag.getDouble("fireballTrail");
        this.maxRadius = compoundTag.getDouble("maxRadius");
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
        this.novaMaxSize = compoundTag.getDouble(FireballAbility.novaRange);
    }

    @Override
    public TagModifierHelper getTag() {
        return new TagModifierHelper(this.elementProjectile.wandAbilityHolder(), FireballAbility.abilityId.getPath().intern());
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        onHitBehaviour();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        if(this.damageEntity(hitEntity)){
            if (GeneralHelpers.Random.nextInt(0, (int) effectChance) == 0) {
                hitEntity.addEffect(new CustomMobEffect(EffectsRegister.FIRE_EFFECT.getDelegate(), (int) effectDuration, (int) effectStrength));
            }

            hitEntity.hurt(this.elementProjectile.damageSources().playerAttack((Player) this.elementProjectile.getOwner()), (float) damage);
            onHitBehaviour();
        }
    }

    @Override
    public void onTickMethod() {
        if(!hasHitLocation){
            elementProjectile.setShowTrailParticles(true);
            elementProjectile.setAnimation(1);
        }

        if(fireballTrail < 0.6) fireballTrail += 0.1; else fireballTrail = 0;
        if(maxRadius < 0.6) maxRadius += 0.1;
        if(aoe >= novaMaxSize) this.elementProjectile.discard();
        if(hasHitLocation || this.elementProjectile.tickCount > 50){
            if(this.elementProjectile.tickCount == 51 && !hasHitLocation) onHitBehaviour();
            fireballNova();
        } else {
            fireball();
            fireballTrailingSound();
        }
    }

    void fireballTrailingSound(){
        if (this.elementProjectile.tickCount % 7 == 0) {
            GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundEvents.FIRE_AMBIENT, 1, 1.5f);
        }
    }

    void fireballNova(){
        if(aoe < novaMaxSize){
            this.fireTrailVegetationBurn();
            if(aoe < 2) aoe *= 1.4; else aoe += 0.45;
            double particle1 = aoe == novaMaxSize ? 0.4 : 0.1  ;
            double particle2 = aoe == novaMaxSize ? 0.2  : Math.max(((10 - aoe) / 40), 0.01);

            if(this.elementProjectile.level() instanceof ServerLevel serverLevel){
                GeneralHelpers.getOuterRingOfRadiusRandom(this.elementProjectile.position(), aoe, 10,
                    positions -> {
                        Vec3 randomisePosition = positions.offsetRandom(RandomSource.create(), (float) 0.6);
                        double vx1 = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.5;
                        GeneralHelpers.generalHelpers.sendParticles(
                            serverLevel,
                            new BakedParticleOptions(this.getElementType().getTypeId(), 5, 4.5f,false),
                            randomisePosition.add(0,0.1,0), 1, vx1, vx1, vx1, particle1
                        );
                    }
                );
                GeneralHelpers.getOuterRingOfRadiusRandom(this.elementProjectile.position(), aoe, 7,
                    positions -> {
                        double vx1 = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.5;
                        GeneralHelpers.generalHelpers.sendParticles(
                            serverLevel,
                            genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 10, 1.5f),
                            positions.add(0,0.1,0), (int) novaMaxSize,  vx1, vx1, vx1, particle2
                        );
                    }
                );
            }
            novaDamageBehaviour();
        }
    }

    void novaDamageBehaviour(){
        this.elementProjectile.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, (LivingEntity) this.elementProjectile.getOwner(),
            this.elementProjectile.getBoundingBox()
                .inflate(aoe,0, aoe)
                .deflate(0,1,0 )
        ).forEach(
            livingEntity -> {
                this.damageEntity(livingEntity);
                if(!this.hitTargets.contains(livingEntity.getUUID())){
                    livingEntity.hurt(
                        this.elementProjectile.damageSources().playerAttack((Player) this.elementProjectile.getOwner()),
                        GeneralHelpers.getMagicDamageAmplifier((LivingEntity) this.elementProjectile.getOwner(), (int) Math.max(this.damage - aoe, 1), this.getElementType())
                    );
                    this.hitTargets.add(livingEntity.getUUID());
                }
            }
        );
    }

    void fireTrailVegetationBurn(){
        BlockPos entityPos = this.elementProjectile.blockPosition();
        for (int x = (int) -aoe; x <= aoe; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = (int) -aoe; z <= aoe ; z++) {

                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= aoe) {
                        BlockPos blockPos = entityPos.offset(x, y, z);
                        BlockState blockState = this.elementProjectile.level().getBlockState(blockPos);
                        SharedFireProperties.fireTrailVegetationRemover(blockState, blockPos, this.elementProjectile, (LivingEntity) this.elementProjectile.getOwner());
                    }
                }
            }
        }
    }

    private void fireball(){
        if(!(this.elementProjectile.level() instanceof ServerLevel serverLevel)) return;
        Vec3 getPositions = GeneralHelpers.getRandomParticleVelocity(this.elementProjectile, 0.1);
        Vec3 getPositions2 = GeneralHelpers.getRandomParticleVelocity(this.elementProjectile, 0.05);
        GeneralHelpers.getRandomSphericalPositions(this.elementProjectile, maxRadius, 20,
            position -> {
                Vec3 newPosition = position.add(this.elementProjectile.getDeltaMovement().scale(-1.5));
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    new BakedParticleOptions(this.getElementType().getTypeId(), 2, 4.5f, false),
                    newPosition.add(0,0.2,0),
                    0, getPositions2.x,getPositions2.y,getPositions2.z,0);
            }
        );
        GeneralHelpers.getSphericalPositions(this.elementProjectile, fireballTrail, 30,
            position -> {
                Vec3 newPosition = position.add(this.elementProjectile.getDeltaMovement().scale(-1.5));
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 4, 2f),
                    newPosition.add(0,0.2,0),
                    1, getPositions.x,getPositions.y,getPositions.z,aoe >= novaMaxSize ? 0.5 : 0.01);
            }
        );
    }

    private void onHitBehaviour() {
        float speed = (float) (this.novaMaxSize/10);
        if(this.elementProjectile.level() instanceof ServerLevel serverLevel){
            ParticleHandlers.spawnPoof(serverLevel, this.elementProjectile.position(), 1,
                genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, this.getElementType(), 40, 3f)
                ,0,0,0,speed
            );

            ParticleHandlers.spawnPoof(serverLevel, this.elementProjectile.position(), 1,
                genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, 40, 3f, rgbToInt(61,61,61), rgbToInt(218,218,218))
                ,0,0,0,speed
            );
        }

        GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.EXPLOSION.get(),2f);
        GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundEvents.FIRE_AMBIENT);

        this.elementProjectile.setDeltaMovement(0,0,0);
        this.hasHitLocation = true;
        this.elementProjectile.setInvisible(true);
        elementProjectile.setShowTrailParticles(false);
        elementProjectile.setAnimation(2);
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("fireball_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new FireBall();
    }
}
