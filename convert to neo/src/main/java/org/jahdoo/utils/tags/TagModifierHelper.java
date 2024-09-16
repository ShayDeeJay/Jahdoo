package org.jahdoo.utils.tags;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.Components.AbilityHolder;

import java.util.HashMap;
import java.util.Map;

import static org.jahdoo.utils.AbilityModifierLuckRoller.getWeightedRandomDouble;
import static org.jahdoo.utils.tags.TagHelper.TagKeys.WAND_STORED_ABILITIES;

public class TagModifierHelper {

    //Mandatory mods
    public static final String MANA_COST = "Mana Cost";
    public static final String COOLDOWN = "Cooldown Duration";

    //Optional mods
    public static final String DAMAGE = "Damage";
    public static final String EFFECT_CHANCE = "Effect Apply Chance";
    public static final String EFFECT_DURATION = "Effect Duration";
    public static final String EFFECT_STRENGTH = "Effect Multiplier";
    public static final String RANGE = "Range";
    public static final String CASTING_DISTANCE = "Cast Distance";
    public static final String SET_ELEMENT_TYPE = "Element Type";
    public static final String LIFETIME = "Life Time";

    WandAbilityHolder wandAbilityHolder = new WandAbilityHolder(new HashMap<>());
    AbilityHolder abilityHolder = new AbilityHolder(new HashMap<>());
    String tagName;
    Player player;
    ItemStack itemStack;

    public TagModifierHelper(){}

    public TagModifierHelper(WandAbilityHolder wandAbilityHolder, String tagName){
        this.wandAbilityHolder = wandAbilityHolder;
        this.tagName = tagName;
    }

    public TagModifierHelper(Player player, ItemStack itemStack){
        this.player = player;
        this.itemStack = itemStack;
    }

    public TagModifierHelper(ItemStack itemStack, String tagName){
        this.tagName = tagName;
        this.itemStack = itemStack;
    }

    private double getLuckModifier(){
        ItemStack itemStack = player.getMainHandItem();
        if(!itemStack.isEmpty() && itemStack.getItem() instanceof Augment){
            Double rating = itemStack.get(DataComponentRegistry.AUGMENT_RATING.get());
            if(rating != null) return Math.max(1, rating); else return 19;
        }
        return 1;
    }

    public void setAbilityTagModifiersRandom(String name, double high, double low, boolean isHigherBetter, double step) {
        double getModifier = getLuckModifier();
        AbilityHolder.AbilityModifiers abilityModifiers = new AbilityHolder.AbilityModifiers(
            getWeightedRandomDouble(high, low, (getModifier == 0) != isHigherBetter, step, getLuckModifier(), 20.0),
            high,
            low,
            isHigherBetter
        );

        this.abilityHolder.abilityProperties().put(name, abilityModifiers);
    }

    public void setAbilityTagModifiers(String name, double high, double low, boolean isHigherBetter, double actualValue) {
        AbilityHolder.AbilityModifiers abilityModifiers = new AbilityHolder.AbilityModifiers(
            actualValue, high, low, isHigherBetter
        );

        this.abilityHolder.abilityProperties().put(name, abilityModifiers);
    }

    public void setDamage(double high, double low, double step){
        this.setAbilityTagModifiersRandom(DAMAGE, high, low, true, step);
    }

    public void setDamageWithValue(double high, double low, double actualValue){
        this.setAbilityTagModifiers(DAMAGE, high, low, true, actualValue);
    }

    public void setMana(double high, double low, double step){

        this.setAbilityTagModifiersRandom(MANA_COST, high, low, false, step);
    }

    public void setManaWithValue(double high, double low, double value){
        this.setAbilityTagModifiers(MANA_COST, high, low, false, value);
    }

    public void setCooldown(double high, double low, double step){
        this.setAbilityTagModifiersRandom(COOLDOWN, high, low, false, step);
    }

    public void setCooldownWithValue(double high, double low, double value){
        this.setAbilityTagModifiers(COOLDOWN, high, low, false, value);
    }

    public void setEffectStrength(double high, double low, double step){
        this.setAbilityTagModifiersRandom(EFFECT_STRENGTH, high, low, true, step);
    }

    public void setEffectStrengthWithValue(double high, double low, double value){
        this.setAbilityTagModifiers(EFFECT_STRENGTH, high, low, false, value);
    }

    public void setEffectDuration(double high, double low, double step){
        this.setAbilityTagModifiersRandom(EFFECT_DURATION, high, low, true, step);
    }

    public void setEffectDurationWithValue(double high, double low, double value){
        this.setAbilityTagModifiers(EFFECT_DURATION, high, low, false, value);
    }

    public void setEffectChance(double high, double low, double step){
        this.setAbilityTagModifiersRandom(EFFECT_CHANCE, high, low, false, step);
    }

    public void setEffectChanceWithValue(double high, double low, double value){
        this.setAbilityTagModifiers(EFFECT_CHANCE, high, low, false, value);
    }

    public void setModifiers(ItemStack itemStack, String abilityId) {
        this.wandAbilityHolder.abilityProperties().put(abilityId, this.abilityHolder);
        itemStack.set(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), this.wandAbilityHolder);
    }

    public void setModifiers(String abilityId) {
        this.wandAbilityHolder.abilityProperties().put(abilityId, this.abilityHolder);
    }

    public WandAbilityHolder getHolder() {
        return this.wandAbilityHolder;
    }

    public void setCastingDistance(double high, double low, double step){
        this.setAbilityTagModifiersRandom(CASTING_DISTANCE, high, low, true, step);
    }

    public void setRange(double high, double low, double step){
        this.setAbilityTagModifiersRandom(RANGE, high, low, true, step);
    }

    public void setLifetime(double high, double low, double step){
        this.setAbilityTagModifiersRandom(LIFETIME, high, low, true, step);
    }

    public double getModifierValue(String name) {
        var allModifiers = wandAbilityHolder.abilityProperties().get(this.tagName);
        var specificValue = allModifiers.abilityProperties();
        return specificValue.get(name).actualValue();
    }
}
