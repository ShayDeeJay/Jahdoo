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

public class Mystic extends AbstractElement {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("mystic");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 4;
    }

    @Override
    public int textColourPrimary() {
        return -6861390;
    }

    @Override
    public int textColourSecondary() {
        return -4103708;
    }

    @Override
    public int particleColourPrimary() {
        return rgbToInt(151, 77, 178);
    }

    @Override
    public int particleColourSecondary() {
        return rgbToInt(193, 97, 228);
    }

    @Override
    public int particleColourFaded() {
        return -12702125;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_MYSTIC.get();
    }

    @Override
    public ElementProperties getParticleGroup() {
        return new ElementProperties(
            bakedParticleFast(this.getTypeId()),
            bakedParticleSlow(this.getTypeId()),
            genericParticleFast(this.particleColourPrimary(), this.particleColourFaded()),
            genericParticleSlow(this.particleColourSecondary(), this.particleColourFaded()),
            genericParticleSlow(this.particleColourPrimary(), this.particleColourFaded()),
            genericParticleFast(this.particleColourSecondary(), this.particleColourFaded())
        );
    }
    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return GeneralHelpers.modResourceLocation("textures/entity/mystic_projectile.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundEvents.AMETHYST_CLUSTER_BREAK;
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.ARCANE_EFFECT.getDelegate();
    }
}
