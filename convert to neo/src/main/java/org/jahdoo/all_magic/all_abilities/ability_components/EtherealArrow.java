package org.jahdoo.all_magic.all_abilities.ability_components;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.utils.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.utils.tags.TagModifierHelper.*;

public class EtherealArrow extends DefaultEntityBehaviour {

    public static ResourceLocation abilityId = GeneralHelpers.modResourceLocation("ethereal_arrow_property");

    double damage;
    double effectDuration;
    double effectStrength;
    double effectChance;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        AbilityHolder abilityHolder = this.genericProjectile.getCompoundTag().abilityProperties().get(abilityId.getPath().intern());
//        this.damage = compoundTag1.getDouble(DAMAGE);
//        this.effectDuration = compoundTag1.getInt(EFFECT_DURATION);
//        this.effectStrength = compoundTag1.getInt(EFFECT_STRENGTH);
//        this.effectChance = compoundTag1.getInt(EFFECT_CHANCE);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(EFFECT_CHANCE, effectChance);
        compoundTag.putDouble(EFFECT_STRENGTH, effectStrength);
        compoundTag.putDouble(EFFECT_DURATION, effectDuration);
        compoundTag.putDouble(DAMAGE, damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    public static CompoundTag setArrowProperties(double damage, int effectDuration, int effectStrength, int effectChance){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putDouble(DAMAGE, damage);
        compoundTag.putInt(EFFECT_DURATION, effectDuration);
        compoundTag.putInt(EFFECT_STRENGTH, effectStrength );
        compoundTag.putInt(EFFECT_CHANCE, effectChance);
        CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.put(EtherealArrow.abilityId.getPath().intern(), compoundTag);
        return compoundTag1;
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {

        if(this.genericProjectile != null){
            GeneralHelpers.getSoundWithPosition(this.genericProjectile.level(), hitEntity.blockPosition(), genericProjectile.getElementType().getElementSound());
            if (hitEntity.isAlive()) {
                if (!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
                spawnElectrifiedParticles(
                    serverLevel, hitEntity.position(),
                    new BakedParticleOptions(
                        genericProjectile.getElementType().getTypeId(),
                        10, 1, false
                    ),
                    3, hitEntity, 0.2

                );

                spawnElectrifiedParticles(
                    serverLevel, hitEntity.position(),
                    genericParticleOptions(genericProjectile.getElementType(), 10, 1.2f)
                    ,3, hitEntity, 0.2
                );
            }

            if (GeneralHelpers.Random.nextInt(0, (int) Math.max(effectChance, 1)) == 0) {
//                hitEntity.addEffect(new CustomMobEffect(genericProjectile.getElementType().elementEffect(), (int) effectDuration, (int) effectStrength));
            }

            if (this.genericProjectile.getOwner() instanceof Player player) {
                GeneralHelpers.damageEntityWithModifiers(hitEntity, player, (float) damage, genericProjectile.getElementType());
            } else {
                hitEntity.hurt(this.genericProjectile.damageSources().mobAttack((LivingEntity) this.genericProjectile.getOwner()), (float) damage);
            }

            this.genericProjectile.discard();
        }
    }

    @Override
    public void onTickMethod() {
        if(this.genericProjectile != null){
            genericProjectile.setDeltaMovement(genericProjectile.getDeltaMovement().subtract(0, 0.02, 0));
            if (genericProjectile.level() instanceof ServerLevel serverLevel) {
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel, ParticleTypes.INSTANT_EFFECT, genericProjectile.position(), 1,
                    0, 0, 0, 0
                );
            }
            playParticles3(
                genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, genericProjectile.getElementType(), 3, 0.08f, true),
                genericProjectile, 20, 0.01
            );
        }
    }

    @Override
    public void discardCondition() {
        if(this.genericProjectile != null){
            if (this.genericProjectile.tickCount > 30) this.genericProjectile.discard();
        }
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new EtherealArrow();
    }
}
