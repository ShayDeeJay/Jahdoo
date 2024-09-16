package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.ArcticStormAbility;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.tags.TagModifierHelper.*;

public class ArcticStorm extends DefaultEntityBehaviour {

    boolean interacted;
    int trackCounter;
    double expandRadius = 0.1;
    private double damage;
    private double effectDuration;
    private double effectStrength;
    private double lifetime;


    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        if(this.aoeCloud.getOwner() != null){
            this.damage = DamageCalculator.getCalculatedDamage(this.aoeCloud.getOwner(), this.getTag().getModifierValue(DAMAGE));
        }
        this.effectDuration = this.getTag().getModifierValue(EFFECT_DURATION);
        this.effectStrength = this.getTag().getModifierValue(EFFECT_STRENGTH);
        this.lifetime = this.getTag().getModifierValue(LIFETIME);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("track_counter", trackCounter);
        compoundTag.putBoolean("interacted", this.interacted);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(LIFETIME, this.lifetime);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.interacted = compoundTag.getBoolean("interacted");
        this.trackCounter = compoundTag.getInt("track_counter");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.lifetime = compoundTag.getDouble(LIFETIME);
    }

    @Override
    public TagModifierHelper getTag() {
        return new TagModifierHelper(this.aoeCloud.getwandabilityholder(), ArcticStormAbility.abilityId.getPath().intern());
    }

    @Override
    public void onTickMethod() {
        trackCounter++;

        if(aoeCloud.getRadius() <= 3) {
            expandRadius *= 1.2;
            aoeCloud.setRadius((float) expandRadius);
        }

        if(aoeCloud.level() instanceof ServerLevel serverLevel){
            this.setSlownessToEntitiesInRadius(aoeCloud, aoeCloud.getOwner(), (int) effectDuration, (int) effectStrength);
            this.setOuterRing(serverLevel);
            this.setOuterRingPulse(serverLevel);
            this.setBlizzard(serverLevel);
        }
    }

    private void setBlizzard(ServerLevel serverLevel){
        GeneralHelpers.getInnerRingOfRadiusRandom(aoeCloud.position(), aoeCloud.getRadius() * 3, aoeCloud.getRadius() * 5,
            positions -> {
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel, genericParticleOptions(this.getElementType(), 40, 2.5f), positions, 1,
                    0, GeneralHelpers.Random.nextDouble(1.1,1.3),0, GeneralHelpers.Random.nextDouble(0.3,0.6)
                );
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    genericParticleOptions(this.getElementType(), 10, 0.2f, true),
                    positions, 1,
                    GeneralHelpers.Random.nextDouble(0.1,0.3),
                    GeneralHelpers.Random.nextDouble(1.1,1.3),
                    GeneralHelpers.Random.nextDouble(0.1,0.3),
                    GeneralHelpers.Random.nextDouble(0.3,0.6)
                );
                if(GeneralHelpers.Random.nextInt(0,30) == 0){
                    GeneralHelpers.getSoundWithPosition(aoeCloud.level(), aoeCloud.blockPosition(), SoundRegister.DASH_EFFECT.get(), 0, 0.3f);
                }
            }
        );
    }

    private void setSlownessToEntitiesInRadius(AoeCloud entity, LivingEntity owner, int effectDuration, int effectStrength){
        GeneralHelpers.getInnerRingOfRadius(entity, entity.getRadius() * 3).forEach(
            positions -> this.setNovaDamage(positions, effectDuration, effectStrength)
        );
    }

    private void setOuterRing(ServerLevel serverLevel){
        GeneralHelpers.getOuterRingOfRadiusRandom(aoeCloud.position(), aoeCloud.getRadius() * 3, Math.max(aoeCloud.getRadius(), 2),
            positions -> {
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    genericParticleOptions(this.getElementType(), 6, 2.5f) ,
                    positions,
                    0, 0, GeneralHelpers.Random.nextDouble(0.1,0.3),0,0.1
                );
            }
        );
    }

    private void setOuterRingPulse(ServerLevel serverLevel){
        if (trackCounter == 20) {
            GeneralHelpers.getOuterRingOfRadiusRandom(aoeCloud.position(), aoeCloud.getRadius() * 3, aoeCloud.getRadius() * 5,
                positions -> {
                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel, genericParticleOptions(this.getElementType(), 12, 2f), positions,
                        0, 0, GeneralHelpers.Random.nextDouble(0.02,0.2),0,1
                    );
                }
            );
            trackCounter = 0;
        }
    }


    private void setNovaDamage(Vec3 positionsA, int effectDuration, int effectAmplifier){
        LivingEntity livingEntity = this.getEntityInRange(positionsA);
        if (livingEntity == null || livingEntity instanceof Player) return;
        livingEntity.addEffect(new CustomMobEffect(EffectsRegister.ICE_EFFECT.getDelegate(), effectDuration, effectAmplifier));
        if (aoeCloud.tickCount % 32 == 0 && aoeCloud.getOwner() instanceof Player player){
            livingEntity.hurt(aoeCloud.damageSources().playerAttack(player), (float) this.damage);
        }
    }

    private LivingEntity getEntityInRange(Vec3 positionsA){
        return aoeCloud.level().getNearestEntity(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            aoeCloud.getOwner(),
            positionsA.x, positionsA.y, positionsA.z,
            new AABB(BlockPos.containing(positionsA)).deflate(1, 4, 1)
        );
    }

    @Override
    public void discardCondition() {
        if (aoeCloud.tickCount > lifetime) {
            aoeCloud.discard();
        }
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.FROST.get();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("arctic_storm_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ArcticStorm();
    }
}
