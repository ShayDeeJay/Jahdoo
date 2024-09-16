package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.MysticalSemtexAbility;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;
import java.util.UUID;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;

public class MysticalSemtex extends DefaultEntityBehaviour {

    private boolean isAttached;
    private int explosionDelay;
    private double aoe = 0.1;
    private LivingEntity target;
    private Vec3 localOffset;
    private UUID targetId;

    double arc;
    double setExplosionDelay;
    double additionalProjectiles;
    double additionalProjectileChance;
    double explosionRadius;
    double damage;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        if(this.elementProjectile.getOwner() != null && !(this.elementProjectile.getOwner() instanceof Player)){
            this.damage = this.getTag().getModifierValue(DAMAGE);
        } else {
            this.damage = DamageCalculator.getCalculatedDamage((Player) this.elementProjectile.getOwner(), this.getTag().getModifierValue(DAMAGE));
        }
        this.arc = this.getTag().getModifierValue(MysticalSemtexAbility.arcs);
        this.setExplosionDelay = this.getTag().getModifierValue(MysticalSemtexAbility.explosionDelays);
        this.additionalProjectiles = this.getTag().getModifierValue(MysticalSemtexAbility.additionalProjectile);
        this.additionalProjectileChance = this.getTag().getModifierValue(MysticalSemtexAbility.additionalProjectileChance);
        this.explosionRadius = this.getTag().getModifierValue(MysticalSemtexAbility.explosionRadius);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("explosion", this.explosionDelay);
        compoundTag.putBoolean("attached", isAttached);
        compoundTag.putDouble(MysticalSemtexAbility.arcs, this.arc);
        compoundTag.putDouble(MysticalSemtexAbility.explosionDelays, this.setExplosionDelay);
        compoundTag.putDouble(MysticalSemtexAbility.additionalProjectile, this.additionalProjectiles);
        compoundTag.putDouble(MysticalSemtexAbility.explosionDelays, this.explosionRadius);
        compoundTag.putDouble(DAMAGE, this.damage);
        if(localOffset != null){
            compoundTag.put("offset", GeneralHelpers.nbtDoubleList(localOffset.x, localOffset.y, localOffset.z));
        }
        if(target != null){
            compoundTag.putUUID("new_target", target.getUUID());
        }
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        ListTag listTag = compoundTag.getList("offset", 6);
        this.arc = compoundTag.getDouble(MysticalSemtexAbility.arcs);
        setExplosionDelay = compoundTag.getDouble(MysticalSemtexAbility.explosionDelays);
        additionalProjectiles = compoundTag.getDouble(MysticalSemtexAbility.additionalProjectile);
        additionalProjectileChance = compoundTag.getDouble(MysticalSemtexAbility.additionalProjectileChance);
        explosionRadius = compoundTag.getDouble(MysticalSemtexAbility.explosionRadius);
        damage = compoundTag.getDouble(DAMAGE);
        this.localOffset = new Vec3(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
        this.explosionDelay = compoundTag.getInt("explosion");
        this.isAttached = compoundTag.getBoolean("attached");
        if(compoundTag.hasUUID("new_target")){
            this.targetId = compoundTag.getUUID("new_target");
        }
    }

    @Override
    public TagModifierHelper getTag() {
//        return new TagModifierHelper(this.elementProjectile.getCompoundTag(), MysticalSemtexAbility.abilityId.getPath().intern());
        return new TagModifierHelper(this.elementProjectile.wandAbilityHolder(), MysticalSemtexAbility.abilityId.getPath().intern());

    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.elementProjectile.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        targetHit(hitEntity);
    }

    @Override
    public void onTickMethod() {
        if(!this.elementProjectile.level().isClientSide){
            if(target == null) elementProjectile.setShowTrailParticles(true);

            if(this.target == null && this.targetId != null ){
                this.target = (LivingEntity) ((ServerLevel) this.elementProjectile.level()).getEntity(this.targetId);
            }

            if (explosionDelay > 0) explosionDelay--;
            adjustProjectileArc();
            attachBombAndFollow();
            onExplosion();
        }
    }

    @Override
    public void discardCondition() {
        if (this.elementProjectile.tickCount > 300) this.elementProjectile.discard();
    }

    private void attachBombAndFollow() {
        if (target != null && this.damageEntity(target)) {
            if (!isAttached) {
                this.elementProjectile.setDeltaMovement(0, 0, 0);
                localOffset = this.elementProjectile.position().subtract(target.position());
                isAttached = true;
            } else {
                this.elementProjectile.setShowTrailParticles(false);
                if(aoe == 0.1){
                    Vec3 newPosition = target.position().add(localOffset);
                    this.elementProjectile.moveTo(newPosition.x, newPosition.y, newPosition.z);
                    if(this.elementProjectile.tickCount % 4 == 0){
                        GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundRegister.TIMER.get());
                        if(this.elementProjectile.level() instanceof ServerLevel serverLevel){
                            ParticleHandlers.spawnPoof(
                                serverLevel,
                                this.elementProjectile.position().add(0,0.2,0),
                                1,
                                this.getElementType().getParticleGroup().magicSlow()
                            );
                        }
                    }
                }
            }
        }
    }

    private void onExplosion() {
        if (target != null && (explosionDelay == 0 || !target.isAlive())) {

            if(aoe == 0.1) {
                elementProjectile.setShowTrailParticles(false);
                this.elementProjectile.setInvisible(true);
                GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundRegister.EXPLOSION.get(),2F, 1F);
                GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundEvents.AMETHYST_BLOCK_BREAK, 1F, 1f);
                additionalProjectileSpread();
            }

            if(aoe < 2) aoe *= 1.5; else aoe += 0.8;

            if(aoe >= explosionRadius) this.elementProjectile.discard();

            nova();
        }

    }

    private void adjustProjectileArc() {
        if (target == null) this.elementProjectile.setDeltaMovement(this.elementProjectile.getDeltaMovement().subtract(0, arc, 0));
    }

    private void nova(){
        if(aoe <= this.explosionRadius){
            double particle1 = aoe >= this.explosionRadius ? 0.4 : 0.1  ;
            double particle2 = aoe >= this.explosionRadius ? 0.2  : Math.max(((10 - aoe) / 40), 0.01);
            novaDamageBehaviour();
            if(!(this.elementProjectile.level() instanceof ServerLevel serverLevel)) return;

            GeneralHelpers.getOuterRingOfRadiusRandom(this.elementProjectile.position(), aoe, 30 - aoe*3,
                positions -> {
                    Vec3 novaAdjusted = positions.add(0,0.3,0).offsetRandom(RandomSource.create(), Math.max(0.5f, (float) (10f - aoe*3)));
                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel,
                        new BakedParticleOptions(this.getElementType().getTypeId(), 2, 3.5f,false),
                        novaAdjusted,
                        Math.max((int) aoe +1, 3), 0, 0, 0, particle1
                    );
                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel,
                        genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 5, 2f),
                        novaAdjusted,
                        (int) (10 - aoe), 0, 0, 0, particle2
                    );
                }
            );
        }
    }

    private void novaDamageBehaviour(){
        this.elementProjectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) this.elementProjectile.getOwner(),
            this.elementProjectile.getBoundingBox()
                .inflate(aoe,0, aoe)
                .deflate(0,1,0 )
        ).forEach(
            livingEntity -> {
                if(this.damageEntity(livingEntity)) livingEntity.hurt(
                    this.elementProjectile.damageSources().playerAttack((Player) this.elementProjectile.getOwner()),
                    (float) Math.max(damage - aoe, 1)
                );
            }
        );
    }

    private void additionalProjectileSpread() {
        if(this.elementProjectile.level() instanceof  ServerLevel serverLevel) {
            ParticleHandlers.spawnPoof(
                serverLevel, this.elementProjectile.position(), 20,
                this.getElementType().getParticleGroup().bakedSlow(), 0, 1, 0, 0.5f
            );
            ParticleHandlers.spawnPoof(
                serverLevel, this.elementProjectile.position(), 20,
                this.getElementType().getParticleGroup().genericSlow(), 0, 1, 0, 0.5f
            );
        }

        if(!elementProjectile.getAdditionalRestriction()){
            if (GeneralHelpers.Random.nextInt(0, (int) this.additionalProjectileChance) == 0) {
                GeneralHelpers.moveEntitiesRelativeToPlayer(this.target, additionalProjectiles,
                    positions -> {
                        ElementProjectile newElementProjectile = new ElementProjectile(
                            EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(),
                            this.target,
                            ProjectilePropertyRegister.MYSTICAL_SEMTEX.get().setAbilityId(),
                            new WandAbilityHolder(Collections.emptyMap())
//                            this.elementProjectile.getCompoundTag()
                        );
                        newElementProjectile.setAdditionalRestrictionBound(true);
                        newElementProjectile.setOwner(this.elementProjectile.getOwner());
                        // move projectile to centre of the source
                        newElementProjectile.moveTo(this.elementProjectile.getX(), this.elementProjectile.getY() + this.elementProjectile.getBbHeight(), this.elementProjectile.getZ());
                        newElementProjectile.shoot(positions.x, positions.y, positions.z, 0.5f, 0);
                        newElementProjectile.setDeltaMovement(newElementProjectile.getDeltaMovement().add(0, arc, 0));
                        this.target.level().addFreshEntity(newElementProjectile);
                    }
                );
                GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.target.blockPosition(), SoundRegister.ORB_FIRE.get(), 0.05f);
            }
        }
    }

    private void targetHit(LivingEntity hitTarget) {
        if (hitTarget instanceof Mob) {
            explosionDelay = (int) setExplosionDelay;
            target = hitTarget;
            elementProjectile.setAnimation(6);
            GeneralHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundEvents.SLIME_BLOCK_BREAK);
        }
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.MYSTIC.get();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("mystical_semtex_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new MysticalSemtex();
    }
}