package org.jahdoo.utils.abilityAttributes;

import net.minecraft.world.entity.LivingEntity;

public class ManaCalculator extends AbstractAttribute{

    public static final String MANA_REDUCTION = "jahdoo_magic_mana_reduction";

    public ManaCalculator(LivingEntity livingEntity, double baseDamage) {
        super(livingEntity, baseDamage);
    }

    @Override
    public String setAttributeIdentifier() {
        return MANA_REDUCTION;
    }

    public static float getCalculatedMana(LivingEntity livingEntity, double baseDamage){
        return (float) new ManaCalculator(livingEntity, baseDamage).getDecreasedValue();
    }

}
