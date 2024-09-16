package org.jahdoo.utils.abilityAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.tags.TagHelper;

import java.util.List;

public abstract class AbstractAttribute {

    protected float attributeModifier;
    protected final double baseValue;
    protected final LivingEntity livingEntity;
    public static final String IS_TYPE_SPECIFIC = "jahdoo_modifier_is_type_specific";
    public static final String GET_ATTRIBUTE_VALUE = "jahdoo_modifier_get_attribute_value";
    protected ItemStack wandItem;

    protected AbstractAttribute(
        LivingEntity livingEntity,
        double baseDamage
    ){
        this.livingEntity = livingEntity;
        this.baseValue = baseDamage;
        wandItem = livingEntity.getMainHandItem();
    }

    public abstract String setAttributeIdentifier();

    public double getIncreasedValue(){
        this.getEquipmentModifierAttributes();
        return this.baseValue + (this.baseValue / 100 * this.attributeModifier);
    }

    public double getIncreasedValueOnTick(){
        this.getEquipmentModifierTick();
        return this.baseValue + (this.baseValue / 100 * this.attributeModifier);
    }

    /**
     * This does not work for mana and cooldown, its very weird, instead of charging less, might be better
     * to instead have the cooldown and mana rate increased instead! manna and cooldown already discounted at ability
     * level.
     * */
    public double getDecreasedValue(){
        this.getEquipmentModifierAttributes();
        return this.baseValue - ((this.baseValue / 100) * this.attributeModifier);
    }

    private void getEquipmentModifierTick(){
        if(!wandItem.isEmpty()){
            getEquipmentSlots().forEach(
                equipmentSlot -> {
                    ItemStack equippedItem = livingEntity.getItemBySlot(equipmentSlot);
//                    CompoundTag getAttributeCompoundTag = equippedItem.getOrCreateTag().getCompound(setAttributeIdentifier());
//                    float getAttributeValue = getAttributeCompoundTag.getFloat(GET_ATTRIBUTE_VALUE);
//                    this.attributeModifier += getAttributeValue;
                }
            );
        }
    }

    private void getEquipmentModifierAttributes(){
        if(!wandItem.isEmpty()){
            getEquipmentSlots().forEach(
                equipmentSlot -> {
                    ItemStack equippedItem = livingEntity.getItemBySlot(equipmentSlot);
                    AbstractElement getWandElement = this.getWandElement();
                    AbstractAbility getCurrentAbility = this.getCastedAbility();
//                    CompoundTag getAttributeCompoundTag = equippedItem.getOrCreateTag().getCompound(setAttributeIdentifier());
//                    boolean isTypeSpecific = getAttributeCompoundTag.getBoolean(IS_TYPE_SPECIFIC);
//                    float getAttributeValue = getAttributeCompoundTag.getFloat(GET_ATTRIBUTE_VALUE);


//                    if (isTypeSpecific) {
//                        if(getWandElement != null){
//                            if (getWandElement == getCurrentAbility.getElemenType()) {
//                                this.attributeModifier += getAttributeValue;
//                            }
//                        }
//                    } else {
//                        this.attributeModifier += getAttributeValue;
//                    }
                }
            );
        }
    }

    private AbstractElement getWandElement(){
        List<AbstractElement> getElement = ElementRegistry.getElementByWandType(wandItem.getItem());
        if(!getElement.isEmpty()){
            return getElement.get(0);
        }
        return null;
    }

    private AbstractAbility getCastedAbility(){
        String getCurrentAbility = TagHelper.getAbilityTypeItemStack(wandItem);
        return AbilityRegister.getSpellsByTypeId(getCurrentAbility).get(0);
    }

    public static void setAttributeToItem(ItemStack itemStack, boolean isTypDependent, float setValue, String tag){
        CompoundTag compoundTag = new CompoundTag();
        // Maybe just go ahead and pass the type here as well so can be displayed later as to what types have what advantages etc;
        compoundTag.putBoolean(IS_TYPE_SPECIFIC, isTypDependent);
        compoundTag.putFloat(GET_ATTRIBUTE_VALUE, setValue);
//        itemStack.getOrCreateTag().put(tag, compoundTag);
    }

    public static CompoundTag setAttributeToCompoundTag(boolean isTypDependent, float setValue){
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean(IS_TYPE_SPECIFIC, isTypDependent);
        compoundTag.putFloat(GET_ATTRIBUTE_VALUE, setValue);
        return compoundTag;
    }

    public static float getAttributeValue(ItemStack itemStack, String tag){
//        CompoundTag compoundTag = itemStack.getOrCreateTag().getCompound(tag);
//        return compoundTag.getFloat(GET_ATTRIBUTE_VALUE);
        return 0;
    }

    public static boolean getAttributeTypeSpecificity(ItemStack itemStack, String tag){
//        CompoundTag compoundTag = itemStack.getOrCreateTag().getCompound(tag);
//        return compoundTag.getBoolean(IS_TYPE_SPECIFIC);
        return false;
    }

    public static List<EquipmentSlot> getEquipmentSlots(){
        return List.of(
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND,
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
        );
    }


}
