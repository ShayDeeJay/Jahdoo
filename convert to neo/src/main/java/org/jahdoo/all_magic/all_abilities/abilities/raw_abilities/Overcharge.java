package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.OverchargedAbility;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;
import java.util.List;

import static org.jahdoo.all_magic.all_abilities.ability_components.LightningTrail.getLightningTrailModifiers;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.ELECTRIC_PARTICLE_SELECTION;
import static org.jahdoo.utils.tags.TagModifierHelper.*;

public class Overcharge extends DefaultEntityBehaviour {

    int privateTicks;
    double orbSize = 1;
    double velocity = 0.2;
    int chance = lifetimeA / 4;
    private static final int lifetimeA = 120;
    private boolean hasHitEntity;

    double damage;
    double gravitationalPull;
    double instability;
    double effectChance;
    double effectDuration;
    double effectStrength;
    CompoundTag compoundTag;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        if(this.elementProjectile.getOwner() != null){
            this.damage = DamageCalculator.getCalculatedDamage((Player) this.elementProjectile.getOwner(), this.getTag().getModifierValue(DAMAGE));
            compoundTag = getLightningTrailModifiers(damage, 2.2, 10, false);
        }
        this.gravitationalPull = this.getTag().getModifierValue(OverchargedAbility.gravitationalPull);
        this.instability = this.getTag().getModifierValue(OverchargedAbility.instability);
        this.effectChance = this.getTag().getModifierValue(EFFECT_CHANCE);
        this.effectDuration = this.getTag().getModifierValue(EFFECT_DURATION);
        this.effectStrength = this.getTag().getModifierValue(EFFECT_STRENGTH);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("orb_size", this.orbSize);
        compoundTag.putDouble("velocity", this.velocity);
        compoundTag.putInt("chance", this.chance);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(OverchargedAbility.gravitationalPull, this.gravitationalPull);
        compoundTag.putDouble(OverchargedAbility.instability, this.instability);
        compoundTag.put("trailProperties", this.compoundTag);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.orbSize = compoundTag.getDouble("orb_size");
        this.velocity = compoundTag.getDouble("velocity");
        this.chance = compoundTag.getInt("chance");
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
        this.gravitationalPull = compoundTag.getDouble(OverchargedAbility.gravitationalPull);
        this.instability = compoundTag.getDouble(OverchargedAbility.instability);
        this.compoundTag = compoundTag.getCompound("trailProperties");
    }

    @Override
    public TagModifierHelper getTag() {
//        return new TagModifierHelper(this.elementProjectile.getCompoundTag(), OverchargedAbility.abilityId.getPath().intern());
        return new TagModifierHelper(this.elementProjectile.wandAbilityHolder(), OverchargedAbility.abilityId.getPath().intern());

    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.elementProjectile.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        this.hasHitEntity = true;
    }

    @Override
    public void onTickMethod() {
        privateTicks++;

        this.applyInertia();
        if (privateTicks > lifetimeA / 1.5) orbSize += 0.02;

        if (privateTicks == 20) {
            this.elementProjectile.setDeltaMovement(0, 0, 0);
        }

        if (privateTicks == 25) {
            GeneralHelpers.getSoundWithPosition(
                this.elementProjectile.level(),
                this.elementProjectile.blockPosition(),
                SoundRegister.ORB_CREATE.get()
            );
        }

        if(privateTicks > 32) {
            orbEnergyParticles();
        }

        if (privateTicks > 25 || this.hasHitEntity) {
            elementProjectile.setShowTrailParticles(false);

            elementProjectile.setAnimation(7);
            if (chance >= instability) chance--;
            if (velocity <= 0.8) velocity += 0.02;
            if (this.elementProjectile.getOwner() != null) {
                pullEntitiesCloser();
                if (privateTicks % Math.min(chance, 20) == 0) {
                    shootSpikesRandomly();
                }
            }
        } else {
            elementProjectile.setShowTrailParticles(true);
        }
    }

    @Override
    public void discardCondition() {if(privateTicks > lifetimeA){
            elementProjectile.setAnimation(8);
            if(privateTicks > lifetimeA + 10) {
                dischargeEffect();
                GeneralHelpers.getSoundWithPosition(
                    this.elementProjectile.level(),
                    this.elementProjectile.blockPosition(),
                    SoundRegister.BOLT.get(),2f,0.5f
                );
                GeneralHelpers.getSoundWithPosition(
                    this.elementProjectile.level(),
                    this.elementProjectile.blockPosition(),
                    SoundRegister.EXPLOSION.get(),2f
                );
                this.elementProjectile.discard();
            }
        }
    }

    private void pullEntitiesCloser(){
        if(this.elementProjectile.getOwner() == null) return;
        List<LivingEntity> nearbyEntities = this.elementProjectile.getOwner().level()
            .getEntitiesOfClass(
                LivingEntity.class,
                this.elementProjectile.getBoundingBox().inflate(gravitationalPull + 5),
                entity -> true
            );
        for (LivingEntity entities : nearbyEntities) {
            GeneralHelpers.entityMoverNoVertical(this.elementProjectile, entities, this.gravitationalPull + 0.5);
        }
    }

    private void shootSpikesRandomly(){
        float speeds = GeneralHelpers.Random.nextFloat((float) velocity - 0.3f, (float) velocity);
        int entitiesShot = 0;
        while (entitiesShot < this.instability) {
            boolean hasBlockBelow = this.elementProjectile.verticalCollisionBelow;
            double theta = GeneralHelpers.Random.nextDouble() * Math.PI *  (hasBlockBelow ? 1 : 4);
            double phi = GeneralHelpers.Random.nextDouble() * Math.PI;
            double x = Math.sin(phi) * Math.cos(theta);
            double y = Math.sin(phi) * Math.sin(theta);
            double z = Math.cos(phi);
            Vec3 newPosition = this.elementProjectile.position().add(this.elementProjectile.getDeltaMovement().scale(4.5));
            if(this.elementProjectile.getOwner() != null){
                GenericProjectile genericProjectile = new GenericProjectile(this.elementProjectile.getOwner(), newPosition.x, newPosition.y, newPosition.z, ProjectilePropertyRegister.LIGHTNING_TRAIL.get().setAbilityId(), new WandAbilityHolder(Collections.emptyMap()), ElementRegistry.LIGHTNING.get());
                GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.BOLT.get(), 0.3f, 0.8f);
                genericProjectile.setOwner(this.elementProjectile.getOwner());
                genericProjectile.shoot(x, y, z, speeds, 0);
                this.elementProjectile.level().addFreshEntity(genericProjectile);
            }
            entitiesShot++;
        }
    }

    private void dischargeEffect(){
        int dischargeEffect = (int) (effectStrength + 5);
        if(this.elementProjectile.level() instanceof  ServerLevel serverLevel){
            this.elementProjectile.level().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                (LivingEntity) this.elementProjectile.getOwner(),
                this.elementProjectile.getBoundingBox().inflate(dischargeEffect)
            ).forEach(
                entities -> {
                    if(this.damageEntity(entities)){
                        if (GeneralHelpers.Random.nextInt(0, chance) == 0) {
                            entities.addEffect(
                                new CustomMobEffect(
                                    EffectsRegister.LIGHTNING_EFFECT.getDelegate(),
                                    (int) this.effectDuration,
                                    (int) this.effectStrength
                                )
                            );
                        }
                    }
                }
            );

            double origin = (double) dischargeEffect / 15;
            double bound = (double) dischargeEffect / 5;

            GeneralHelpers.generalHelpers.sendParticles(serverLevel,

                genericParticleOptions(ELECTRIC_PARTICLE_SELECTION, this.getElementType(), GeneralHelpers.Random.nextInt(5,10),5, 1.2),
                this.elementProjectile.position(),
                dischargeEffect * 5,
                GeneralHelpers.Random.nextDouble(origin, bound),
                GeneralHelpers.Random.nextDouble(origin, bound),
                GeneralHelpers.Random.nextDouble(origin, bound),
                0
            );

            GeneralHelpers.generalHelpers.sendParticles(serverLevel,
                new BakedParticleOptions(this.getElementType().getTypeId(), 4, 3, false),
                this.elementProjectile.position(),
                dischargeEffect * 5,
                GeneralHelpers.Random.nextDouble(origin, bound),
                GeneralHelpers.Random.nextDouble(origin, bound),
                GeneralHelpers.Random.nextDouble(origin, bound),
                0.6
            );
        }
    }

    public void applyInertia() {
        double inertiaFactor = 0.96; // Adjust this value to control the rate of slowdown (0.98 means 2% reduction per tick)
        Vec3 currentVelocity = this.elementProjectile.getDeltaMovement();

        // Reduce the velocity by the inertia factor
        double newVelocityX = currentVelocity.x * inertiaFactor;
        double newVelocityY = currentVelocity.y * inertiaFactor;
        double newVelocityZ = currentVelocity.z * inertiaFactor;

        // Apply the new velocity to the this.elementthis.elementProjectile
        this.elementProjectile.setDeltaMovement(newVelocityX, newVelocityY, newVelocityZ);
    }

    void orbEnergyParticles(){
        if(!(this.elementProjectile.level() instanceof ServerLevel serverLevel)) return;

        Vec3 velocityA = GeneralHelpers.getRandomParticleVelocity(this.elementProjectile, 0.1);
        Vec3 velocityB = GeneralHelpers.getRandomParticleVelocity(this.elementProjectile, 0.05);
        double reducedPointsInRadius = (double) privateTicks / 40;

        GenericParticleOptions particleOptionsOne = genericParticleOptions(ELECTRIC_PARTICLE_SELECTION, this.getElementType(), 10,2);
        BakedParticleOptions particleOptionsTwo = new BakedParticleOptions(this.getElementType().getTypeId(), 8, 2.5f, false);

        GeneralHelpers.getRandomSphericalPositions(this.elementProjectile, this.orbSize,  reducedPointsInRadius,
            position -> GeneralHelpers.generalHelpers.sendParticles(
                serverLevel, particleOptionsOne, position.add(0,0.2,0), 0,
                velocityA.x, velocityA.y, velocityA.z, 0.1
            )
        );

        GeneralHelpers.getRandomSphericalPositions(this.elementProjectile, this.orbSize,  reducedPointsInRadius * 5,
            position -> GeneralHelpers.generalHelpers.sendParticles(
                serverLevel, particleOptionsTwo, position.add(0,0.2,0), 1,
                velocityB.x, velocityB.y, velocityB.z, privateTicks >= 300 ? 0.5 : 0.2
            )
        );
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.LIGHTNING.get();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("overcharge_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Overcharge();
    }
}
