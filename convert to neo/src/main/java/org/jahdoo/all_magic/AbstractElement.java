package org.jahdoo.all_magic;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import org.jahdoo.utils.GeneralHelpers;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractElement {
    private String elementId = null;

    public final String setAbilityId() {
        if (elementId == null) {
            var resourceLocation = Objects.requireNonNull(getAbilityResource());
            elementId = resourceLocation.getPath().intern();
        }

        return elementId;
    }

    public abstract ResourceLocation getAbilityResource();

    public String getElementName(){
        return GeneralHelpers.stringIdToName(elementId);
    }

    public abstract int getTypeId();

    public abstract int textColourPrimary();

    public abstract int textColourSecondary();

    public abstract int particleColourPrimary();

    public abstract int particleColourSecondary();

    public abstract int particleColourFaded();

    @Nullable
    public abstract Item getWand();

    public abstract ElementProperties getParticleGroup();

    @Nullable
    public abstract ResourceLocation getAbilityProjectileTexture();

    public abstract SoundEvent getElementSound();

    public abstract Holder<MobEffect> elementEffect();

}
