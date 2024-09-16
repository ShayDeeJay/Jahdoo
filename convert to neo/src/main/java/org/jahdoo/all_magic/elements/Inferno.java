package org.jahdoo.all_magic.elements;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.ElementProperties;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleStore.*;

public class Inferno extends AbstractElement {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("inferno");

    @Override
    public String getElementName() {
        return abilityId.getPath().intern().substring(0,1).toUpperCase() + abilityId.getPath().intern().substring(1);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public int textColourPrimary() {
        return -48128;
    }

    @Override
    public int textColourSecondary() {
        return -34487;
    }

    @Override
    public int particleColourPrimary() {
        return rgbToInt(255, 68, 0);
    }

    @Override
    public int particleColourSecondary() {
        return rgbToInt(255, 121, 73);
    }

    @Override
    public int particleColourFaded() {
        return -34487;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_INFERNO.get();
    }

    @Override
    public ElementProperties getParticleGroup() {
        return new ElementProperties(
            bakedParticleFast(this.getTypeId()),
            bakedParticleSlow(this.getTypeId()),
            genericParticleFast(this.particleColourPrimary(), this.particleColourFaded()),
            genericParticleSlow(this.particleColourSecondary(), this.particleColourFaded()),
            genericParticleFast(this.particleColourPrimary(), this.particleColourFaded()),
            genericParticleFast(this.particleColourSecondary(), this.particleColourFaded())
        );
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return GeneralHelpers.modResourceLocation("textures/entity/fire_projectile.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundEvents.FIRECHARGE_USE;
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.FIRE_EFFECT.getDelegate();
    }
}
