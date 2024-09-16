package org.jahdoo.all_magic.effects.custom_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;
import java.util.Optional;

public class ItemMagnetEffect extends MobEffect {


    public ItemMagnetEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {

        List<ItemEntity> items = targetEntity.level().getEntitiesOfClass(
            ItemEntity.class,
            targetEntity.getBoundingBox().inflate(pAmplifier),
            entity -> true
        );

        List<ExperienceOrb> experienceOrbs = targetEntity.level().getEntitiesOfClass(
            ExperienceOrb.class,
            targetEntity.getBoundingBox().inflate(pAmplifier),
            entity -> true
        );

        for (ItemEntity item : items) {
            GeneralHelpers.entityMover(targetEntity, item, pAmplifier);
        }

        for (ExperienceOrb experience : experienceOrbs) {
            GeneralHelpers.entityMover(targetEntity, experience, pAmplifier);
        }

        return true;
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
