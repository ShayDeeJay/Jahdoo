package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.QuantumDestroyerAbility;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;
import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;
import static org.jahdoo.utils.tags.TagModifierHelper.LIFETIME;

public class QuantumDestroyer extends DefaultEntityBehaviour {
    private double counter = 1;
    private int privateTicks;
    private boolean isFullForm;

    double radius;
    double damage;
    double lifetime;
    double gravitationalPull;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        this.radius = this.getTag().getModifierValue(QuantumDestroyerAbility.radius);
        this.gravitationalPull = this.getTag().getModifierValue(QuantumDestroyerAbility.gravitationalPull);
        this.lifetime = this.getTag().getModifierValue(LIFETIME);
        if(this.elementProjectile.getOwner() != null){
            this.damage = DamageCalculator.getCalculatedDamage((Player) this.elementProjectile.getOwner(), this.getTag().getModifierValue(DAMAGE));
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("counter", this.counter);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putBoolean("full_form", this.isFullForm);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(LIFETIME, this.lifetime);
        compoundTag.putDouble(QuantumDestroyerAbility.radius, this.radius);
        compoundTag.putDouble(QuantumDestroyerAbility.gravitationalPull, this.gravitationalPull);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.counter = compoundTag.getDouble("counter");
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.isFullForm = compoundTag.getBoolean("full_form");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.lifetime = compoundTag.getDouble(LIFETIME);
        this.radius = compoundTag.getDouble(QuantumDestroyerAbility.radius);
        this.gravitationalPull = compoundTag.getDouble(QuantumDestroyerAbility.gravitationalPull);
    }

    @Override
    public TagModifierHelper getTag() {
//        return new TagModifierHelper(this.elementProjectile.getCompoundTag(), QuantumDestroyerAbility.abilityId.getPath().intern());
        return new TagModifierHelper(this.elementProjectile.wandAbilityHolder(), QuantumDestroyerAbility.abilityId.getPath().intern());
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.elementProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        privateTicks++;
        if(!(this.elementProjectile.level() instanceof ServerLevel serverLevel)) return;
        if (privateTicks < 20) {
            elementProjectile.setShowTrailParticles(true);
            if(privateTicks == 1) this.entitySpawnParticles(serverLevel);
            if(this.elementProjectile.tickCount % 4 == 0) {
                GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.TIMER.get(), 1.5f, 1.2f);
                this.entitySpawnParticles(serverLevel);
            }
            this.elementProjectile.setDeltaMovement(0, 0.5, 0);

        } else {
            if(!isFullForm){
                isFullForm = true;
                GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.ORB_CREATE.get(), 1.5f, 0.8f);
                ParticleHandlers.spawnPoof(
                    serverLevel, this.elementProjectile.position(), 20,
                    genericParticleOptions(MAGIC_PARTICLE_SELECTION, this.getElementType(), 15,4),

                    0,0,0,1f
                );
                this.elementProjectile.setDeltaMovement(0, 0, 0);
            }

            if(privateTicks <= lifetime){
                if(counter < radius) counter *= 1.6;
                elementProjectile.setAnimation(4);
                playAmbientSound();
                gravityEffect();
                damageCalculator();
            }

            particle();
        }
    }

    @Override
    public void discardCondition() {
        if (privateTicks > lifetime) {
            if (this.elementProjectile.tickCount == lifetime + 1) {
                elementProjectile.setAnimation(5);
                GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundEvents.ENDER_EYE_DEATH, 2.5F, 0.8F);
            }

            if(privateTicks > lifetime + 6) this.elementProjectile.discard();
        }
    }

    private void entitySpawnParticles(ServerLevel serverLevel){
        int particleCount = 2;
        float speed = 0.05f;

        ParticleHandlers.spawnPoof(
            serverLevel, this.elementProjectile.position(), particleCount,
            new BakedParticleOptions(this.getElementType().getTypeId(), 20,2,false),
            0,0,0,speed
        );
        ParticleHandlers.spawnPoof(
            serverLevel, this.elementProjectile.position(), particleCount,
            genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 20,2),
            0,0,0,speed
        );
    }

    private void playAmbientSound(){

        if (privateTicks == 21) {
            GeneralHelpers.getSoundWithPosition(
                this.elementProjectile.level(), this.elementProjectile.blockPosition(),
                SoundEvents.ELDER_GUARDIAN_AMBIENT, 1.5f, 0.6f
            );
        }

        if(privateTicks < this.lifetime - 30){
            if(this.elementProjectile.tickCount % 40 == 0){
                GeneralHelpers.getSoundWithPosition(
                    this.elementProjectile.level(), this.elementProjectile.blockPosition(),
                    SoundEvents.ELDER_GUARDIAN_AMBIENT, 1.5f, 0.6f
                );
            }
        }

        if(GeneralHelpers.Random.nextInt(0, 20) == 0){
            GeneralHelpers.getSoundWithPosition(
                this.elementProjectile.level(), this.elementProjectile.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 0.1f
            );
        }
    }

    private void particle(){

        if(!(this.elementProjectile.level() instanceof ServerLevel serverLevel)) return;
        boolean explode = privateTicks > lifetime;
        GeneralHelpers.getRandomSphericalPositions(this.elementProjectile, counter, counter * 8,
            position -> {
                Vec3 directions = this.elementProjectile.position().subtract(position).normalize();

                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, this.getElementType(), 10,GeneralHelpers.Random.nextInt(3,4)),
                    position, 0,
                    directions.x, directions.y, directions.z, explode ? 1.2 : 0.8
                );
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,  new BakedParticleOptions(
                        this.getElementType().getTypeId(),
                        10,
                        GeneralHelpers.Random.nextInt(3,5),
                        false
                    ),
                    position, GeneralHelpers.Random.nextInt(0,2),
                    directions.x, directions.y, directions.z, explode ? 1.2 : 0.4
                );
            }
        );
    }

    private void gravityEffect(){
        List<LivingEntity> nearbyEntities = this.elementProjectile.level().getEntitiesOfClass(
            LivingEntity.class,
            this.elementProjectile.getBoundingBox().inflate(radius * 3),
            entity -> true
        );

        for (LivingEntity entities : nearbyEntities) {
            if(this.damageEntity(entities)){
                GeneralHelpers.entityMover(this.elementProjectile, entities, gravitationalPull + 0.6);
            }
        }
    }

    private void damageCalculator(){
        if(this.elementProjectile.getOwner() != null){
            this.elementProjectile.level().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                (LivingEntity) this.elementProjectile.getOwner(),
                this.elementProjectile.getBoundingBox().inflate(radius)
            ).forEach(
                livingEntity -> {
                    if (this.damageEntity(livingEntity)) {
                        livingEntity.hurt(
                            this.elementProjectile.damageSources().playerAttack((Player) this.elementProjectile.getOwner()),
                            (float) damage
                        );
                    }
                }
            );
        }
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.MYSTIC.get();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("quantum_destroyer_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new QuantumDestroyer();
    }
}
