package org.jahdoo.capabilities.internal_systems;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import org.jahdoo.capabilities.AbstractCapability;

import java.util.Map;

public class AbilityCooldownSystem implements AbstractCapability {

    private static final String NBT_KEY = "jahdoo_cooldowns";

    private Map<String, Integer> abilityCooldowns = new Object2IntOpenHashMap<>();

    public Map<String, Integer> getAllCooldowns() {
        return abilityCooldowns;
    }

    public void addCooldown(String ability, int cooldown){
        abilityCooldowns.put(ability, cooldown);
    }

    public int getCooldown(String abilityId){
        return this.abilityCooldowns.get(abilityId);
    }

    public boolean isAbilityOnCooldown(String abilityId){
        return this.abilityCooldowns.containsKey(abilityId);
    }

    public void setLocalCooldowns(Map<String, Integer> abilityCooldowns){
        this.abilityCooldowns = abilityCooldowns;
    }

    public void applyAllCooldowns(){
        if(abilityCooldowns.isEmpty()) return;
        abilityCooldowns.forEach(
            (ability, cooldown) -> {
                if(cooldown > 0) abilityCooldowns.put(ability, cooldown - 1); else abilityCooldowns.remove(ability);
            }
        );
    }

    public void removeAbilityFromCooldown(String ability){
        this.abilityCooldowns.remove(ability);
    }

    public void saveNBTData(CompoundTag nbt) {
        CompoundTag compoundTag = new CompoundTag();
        this.abilityCooldowns.forEach(compoundTag::putInt);
        nbt.put(NBT_KEY, compoundTag);
    }

    public void loadNBTData(CompoundTag nbt) {
        nbt.getCompound(NBT_KEY).getAllKeys().forEach(
            keys -> abilityCooldowns.put(keys, nbt.getCompound(NBT_KEY).getInt(keys))
        );
    }

    @Override
    public void copyFrom(AbstractCapability source) {
        if(source instanceof AbilityCooldownSystem abilityCooldownSystem){
            this.abilityCooldowns = abilityCooldownSystem.abilityCooldowns;
        }
    }

}
