package org.jahdoo.capabilities.internal_systems;

import com.ibm.icu.text.DecimalFormat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.capabilities.AbstractCapability;
import org.jahdoo.utils.abilityAttributes.ManaRegen;

public class ManaSystem implements AbstractCapability {

    private static final String NBT_KEY = "jahdoo_mana";

    private double manaPool;
    private double manaRegenSpeed;

    private final double baseRegenSpeed = 0.02;
    private final int MIN_MANA = 0;
    private final int MAX_MANA = 100;

    public double getManaPool() {
        return manaPool;
    }

    public int getMaxMana(){
        return this.MAX_MANA;
    }

    public void subtractMana(double regenMana) {
        this.manaPool = Math.max(manaPool - regenMana, MIN_MANA);
    }

    public void onTick(Player player){
        this.regenMana();
        this.getEquipmentModifierAttributes(player);
    }

    private void getEquipmentModifierAttributes(Player player){
        manaRegenSpeed = ManaRegen.getCalculatedMana(player, baseRegenSpeed);
    }

    public void regenMana() {
        this.manaPool = Math.min(manaPool + manaRegenSpeed, MAX_MANA);
    }

    public void setLocalMana(double manaPool){
        this.manaPool = manaPool;
    }

    @Override
    public void saveNBTData(CompoundTag nbt) {
        nbt.putDouble(NBT_KEY, manaPool);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        manaPool = nbt.getDouble(NBT_KEY);
    }

    @Override
    public void copyFrom(AbstractCapability source) {
        if(source instanceof ManaSystem manaSystem){
            this.manaPool = manaSystem.manaPool;
        }
    }

}
