package org.jahdoo.all_magic.elements;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.ElementProperties;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleStore.*;

public class Lightning extends AbstractElement {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("lightning");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 3;
    }

    @Override
    public int textColourPrimary() {
        return rgbToInt(80, 161, 160);
    }

    @Override
    public int textColourSecondary() {
        return rgbToInt(197,225,224);
    }

    @Override
    public int particleColourPrimary() {
        return rgbToInt(110, 176, 186);
    }

    @Override
    public int particleColourSecondary() {
        return rgbToInt(220, 233, 235);
    }

    @Override
    public int particleColourFaded() {
        return rgbToInt(226, 252, 255);
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_LIGHTNING.get();
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
        return GeneralHelpers.modResourceLocation("textures/entity/lightning_projectile2.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundRegister.BOLT.get();
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.LIGHTNING_EFFECT.getDelegate();
    }
}
