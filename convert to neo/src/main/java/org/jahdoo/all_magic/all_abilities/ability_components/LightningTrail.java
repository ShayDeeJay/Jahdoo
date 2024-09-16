package org.jahdoo.all_magic.all_abilities.ability_components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;
import static org.jahdoo.utils.tags.TagModifierHelper.LIFETIME;

public class LightningTrail extends DefaultEntityBehaviour {
    double randomFactor;
    public static final String EASING = "ease";
    public static final String SHOULD_EASE = "should_ease";

    double damage;
    double easing;
    double lifetime;
    boolean shouldEase;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
//        if(this.genericProjectile.getOwner() != null){
//            this.damage = getCompoundProperties().getDouble(DAMAGE);
//        }
//        this.easing = getCompoundProperties().getDouble(EASING);
//        this.lifetime = getCompoundProperties().getDouble(LIFETIME);
//        this.shouldEase = getCompoundProperties().getBoolean(SHOULD_EASE);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(EASING, this.easing);
        compoundTag.putDouble(LIFETIME, this.lifetime);
        compoundTag.putBoolean(SHOULD_EASE, this.shouldEase);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.damage = compoundTag.getDouble(DAMAGE);
        this.easing = compoundTag.getDouble(EASING);
        this.lifetime = compoundTag.getDouble(LIFETIME);
        this.shouldEase = compoundTag.getBoolean(SHOULD_EASE);
    }

    public static CompoundTag getLightningTrailModifiers(double damage, double easing, double lifetime, boolean shouldEase){
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag compoundTag1 = new CompoundTag();
        compoundTag.putDouble(DAMAGE, damage);
        compoundTag.putDouble(EASING, easing);
        compoundTag.putDouble(LIFETIME, lifetime);
        compoundTag.putBoolean(SHOULD_EASE, shouldEase);
        compoundTag1.put(abilityId.getPath().intern(), compoundTag);
        return compoundTag1;
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.LIGHTNING.get();
    }

    private WandAbilityHolder getWandAbility(){
        return this.genericProjectile.wandAbilityHolder();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        hitEntity.hurt(
            this.genericProjectile.damageSources().playerAttack((Player) this.genericProjectile.getOwner()),
            (float) damage
        );
    }

    @Override
    public void onTickMethod() {
        if(shouldEase){
            if (this.randomFactor < 2) this.randomFactor += easing;
        } else {
            this.randomFactor = easing;
        }

        if(this.genericProjectile != null){
            moveLikeLightningBolt(this.genericProjectile);
            ParticleHandlers.GenericProjectile(
                genericProjectile,
                new BakedParticleOptions(getElementType().getTypeId(), 1, 0.3f, true),
                genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, ElementRegistry.LIGHTNING.get(), 2, 1.5f),
                0.015
            );
        }
    }

    @Override
    public void discardCondition() {
        if(this.genericProjectile != null){
            if (genericProjectile.tickCount > GeneralHelpers.Random.nextInt((int) (lifetime - 2), (int) lifetime)) {
                genericProjectile.discard();
            }
        }
    }

    private void moveLikeLightningBolt(Projectile projectile) {
        Vec3 currentMovement = projectile.getDeltaMovement();
        if (GeneralHelpers.Random.nextDouble() < 0.98) {
            double dx = (GeneralHelpers.Random.nextDouble() - 0.5) * randomFactor;
            double dy = (GeneralHelpers.Random.nextDouble() - 0.5) * randomFactor;
            double dz = (GeneralHelpers.Random.nextDouble() - 0.5) * randomFactor;
            Vec3 newMovement = currentMovement.add(dx, dy, dz).normalize().scale(currentMovement.length());
            projectile.setDeltaMovement(newMovement);
        }

        Vec3 vec3 = projectile.getDeltaMovement();
        projectile.setPos(projectile.getX() + vec3.x, projectile.getY() + vec3.y, projectile.getZ() + vec3.z);
    }

    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("lightning_trail_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new LightningTrail();
    }
}
