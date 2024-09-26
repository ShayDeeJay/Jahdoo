package org.jahdoo.capabilities;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.networking.packet.CooldownsDataSyncS2CPacket;
import org.jahdoo.networking.packet.ManaDataSyncS2CPacket;
import org.jahdoo.utils.abilityAttributes.ManaRegen;

import java.util.Map;

import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class CastingData {

    private static final String mana = "jahdoo_magic_data_mana";
    private static final String cooldowns = "jahdoo_magic_data_cooldowns";
    //Mana system
    private double manaPool;
    private double manaRegenSpeed;

    private final double baseRegenSpeed = 0.1;
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

    public void manaRegen(Player player){
        this.regenMana(player);
    }

    private double getModifiedMana(Player player){
        return ManaRegen.getCalculatedMana(player, baseRegenSpeed);
    }

    public void regenMana(Player player) {
        this.manaPool = Math.min(manaPool + getModifiedMana(player), MAX_MANA);
    }

    public void setLocalMana(double manaPool){
        this.manaPool = manaPool;
    }

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        this.abilityCooldowns.forEach(compoundTag::putInt);
        nbt.put(cooldowns, compoundTag);
        nbt.putDouble(mana, manaPool);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        manaPool = nbt.getDouble(mana);
        nbt.getCompound(cooldowns).getAllKeys().forEach(
            keys -> abilityCooldowns.put(keys, nbt.getCompound(cooldowns).getInt(keys))
        );
    }

    public static void manaTickEvent(ServerPlayer serverPlayer) {
        var magicData = serverPlayer.getData(CASTER_DATA);
        magicData.manaRegen(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new ManaDataSyncS2CPacket(magicData.getManaPool()));
    }

    //Cooldown System

    private Map<String, Integer> abilityCooldowns = new Object2IntOpenHashMap<>();

    public static void cooldownTickEvent(ServerPlayer serverPlayer){
        var cooldowns = serverPlayer.getData(CASTER_DATA);
        cooldowns.applyAllCooldowns();
        PacketDistributor.sendToPlayer(serverPlayer, new CooldownsDataSyncS2CPacket(cooldowns.getAllCooldowns()));
    }

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

}
