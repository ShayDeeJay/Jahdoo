package org.jahdoo.utils.abilityAttributes;

import net.minecraft.world.entity.LivingEntity;

public class DamageCalculator extends AbstractAttribute {

    public static final String DAMAGE_MULTIPLIER = "jahdoo_magic_damage_amplifier";

    public DamageCalculator(LivingEntity livingEntity, double baseDamage) {
        super(livingEntity, baseDamage);
    }

    @Override
    public String setAttributeIdentifier() {
        return DAMAGE_MULTIPLIER;
    }

    public static float getCalculatedDamage(LivingEntity livingEntity, double baseDamage){
        return (float) new DamageCalculator(livingEntity, baseDamage).getIncreasedValue();
    }

}
