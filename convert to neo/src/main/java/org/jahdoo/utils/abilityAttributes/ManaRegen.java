package org.jahdoo.utils.abilityAttributes;

import net.minecraft.world.entity.LivingEntity;

public class ManaRegen extends AbstractAttribute{

    public static final String MANA_REGENERATION = "jahdoo_magic_mana_regen";

    public ManaRegen(LivingEntity livingEntity, double baseDamage) {
        super(livingEntity, baseDamage);
    }

    @Override
    public String setAttributeIdentifier() {
        return MANA_REGENERATION;
    }

    public static float getCalculatedMana(LivingEntity livingEntity, double baseDamage){
        return (float) new ManaRegen(livingEntity, baseDamage).getIncreasedValueOnTick();
    }

}
