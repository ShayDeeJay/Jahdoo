package org.jahdoo.all_magic.effects.custom_effects.type_effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jahdoo.all_magic.effects.EffectParticles;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;

import java.util.Optional;

public class IceEffect extends MobEffect {
    public IceEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {
        if(targetEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = GeneralHelpers.Random.nextInt(0,20);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.withDefaultNamespace("jahdoo.movement_speed"), -((double) pAmplifier / 100), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

            EffectParticles.setEffectParticle(getRandomChance, targetEntity, serverLevel, ElementRegistry.FROST.get(), SoundRegister.ICE_ATTACH.get());
        }
        return true;
    }

    @Override
    public MobEffectCategory getCategory() {
        return MobEffectCategory.HARMFUL;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }
}
