package org.jahdoo.utils.abilityAttributes;

import net.minecraft.world.entity.LivingEntity;

public class CooldownCalculator extends AbstractAttribute{

    public static final String COOLDOWN_REDUCTION = "jahdoo_magic_cooldown_reduction";

    public CooldownCalculator(LivingEntity livingEntity, double baseDamage) {
        super(livingEntity, baseDamage);
    }

    @Override
    public String setAttributeIdentifier() {
        return COOLDOWN_REDUCTION;
    }

    public static float getCalculatedCooldown(LivingEntity livingEntity, double baseDamage){
        return (float) new CooldownCalculator(livingEntity, baseDamage).getDecreasedValue();
    }

}
