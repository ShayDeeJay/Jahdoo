package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.ElementalShooterAbility;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;

import static org.jahdoo.items.augments.AugmentItemHelper.throwNewItem;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.tags.TagModifierHelper.*;

public class ElementalShooter extends DefaultEntityBehaviour {
    private int blockBounce;
    double numberOfRicochets;
    double effectChance;
    double effectStrength;
    double effectDuration;
    double damage;


    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.numberOfRicochets = getTag().getModifierValue(ElementalShooterAbility.numberOfRicochet);
        this.effectChance = getTag().getModifierValue(EFFECT_CHANCE);
        this.effectStrength = getTag().getModifierValue(EFFECT_STRENGTH);
        this.effectDuration = getTag().getModifierValue(EFFECT_DURATION);
        if(this.genericProjectile.getOwner() != null){
            this.damage = DamageCalculator.getCalculatedDamage((Player) this.genericProjectile.getOwner(), this.getTag().getModifierValue(DAMAGE));
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("blockBounce", this.blockBounce);
        compoundTag.putDouble(ElementalShooterAbility.numberOfRicochet, this.numberOfRicochets);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.blockBounce = compoundTag.getInt("blockBounce");
        this.numberOfRicochets = compoundTag.getDouble(ElementalShooterAbility.numberOfRicochet);
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    @Override
    public TagModifierHelper getTag() {
//        return new TagModifierHelper(this.genericProjectile.getCompoundTag(), ElementalShooterAbility.abilityId.getPath().intern());
        return new TagModifierHelper(this.genericProjectile.getCompoundTag(), ElementalShooterAbility.abilityId.getPath().intern());

    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
        if(blockBounce == numberOfRicochets) this.genericProjectile.discard();
//        AbstractElement element = getElement((LivingEntity) this.genericProjectile.getOwner(), this.genericProjectile.getCompoundTag());

//        GeneralHelpers.getSoundWithPosition(this.genericProjectile.level(), this.genericProjectile.blockPosition(), element.getElementSound(), 0.4f);
//        ParticleHandlers.spawnPoof(serverLevel, this.genericProjectile.position(), 2, element.getParticleGroup().bakedSlow());
        this.setReboundBehaviour(blockHitResult);
    }

//    private AbstractElement getElement(LivingEntity owner, CompoundTag compoundTag){
//        double typeAlone = compoundTag.getCompound(ElementalShooterAbility.abilityId.getPath().intern()).getInt(SET_ELEMENT_TYPE);
//
//        double typeInWand = TagHelper.getWandAbilities(ElementalShooterAbility.abilityId.getPath().intern(), (Player) owner).getInt(SET_ELEMENT_TYPE);
//        double type = typeAlone == 0 ? typeInWand : typeAlone;
//        List<AbstractElement> getElement = ElementRegistry.getElementByTypeId((int) type);
//        if(!getElement.isEmpty()) return getElement.get(0);
//        return ElementRegistry.MYSTIC.get();
//    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
//        AbstractElement element = getElement((LivingEntity) this.genericProjectile.getOwner(), this.genericProjectile.getCompoundTag());
        if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
//        ParticleHandlers.spawnPoof(serverLevel, this.genericProjectile.position(), 1, element.getParticleGroup().bakedSlow());
//        this.applyEffect(hitEntity, element.elementEffect());
//        this.setDamageByOwner(hitEntity, element);
        this.genericProjectile.discard();
    }

    @Override
    public void onTickMethod() {
//        AbstractElement element = getElement((LivingEntity) this.genericProjectile.getOwner(), this.genericProjectile.getCompoundTag());
//        ParticleHandlers.GenericProjectile(this.genericProjectile,
//            new BakedParticleOptions(element.getTypeId(), 2, 0.25f, true),
//            genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, element, 5, 1f),
//            0.015
//        );
    }

    @Override
    public void discardCondition() {
//        AbstractElement element = getElement((LivingEntity) this.genericProjectile.getOwner(), this.genericProjectile.getCompoundTag());
//        if (this.genericProjectile.getOwner() != null && this.genericProjectile.distanceTo(this.genericProjectile.getOwner()) > 50f) {
//            if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
//            ParticleHandlers.spawnPoof(serverLevel, this.genericProjectile.position(), 1, element.getParticleGroup().bakedSlow());
//            this.genericProjectile.discard();
//        }
    }


    private void setReboundBehaviour(BlockHitResult blockHitResult){
        Vec3 normal = Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal());
        Vec3 motion = this.genericProjectile.getDeltaMovement();
        Vec3 reflection = motion.subtract(normal.scale(2 * motion.dot(normal)));
        this.genericProjectile.setDeltaMovement(reflection);
        blockBounce++;
    }


    private void applyEffect(LivingEntity livingEntity, MobEffect mobEffect){
        if(GeneralHelpers.Random.nextInt(0, this.effectChance == 0 ? 1 : (int) this.effectChance) == 0){
//            livingEntity.addEffect(new CustomMobEffect(mobEffect, (int) effectDuration, (int) effectStrength));
        }
    }

    private void setDamageByOwner(LivingEntity target, AbstractElement element){
        if (this.genericProjectile.getOwner() != null) {
            target.hurt(
                this.genericProjectile.damageSources().playerAttack((Player) this.genericProjectile.getOwner()),
                GeneralHelpers.getMagicDamageAmplifier((Player) this.genericProjectile.getOwner(), (float) damage, element)
            );
        } else  {
            target.hurt(this.genericProjectile.damageSources().magic(), (float) damage);
        }
        if(!target.isAlive()) throwNewItem(target, new ItemStack(ItemsRegister.AUGMENT_ITEM.get()));
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("elemental_shooter_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ElementalShooter();
    }
}
