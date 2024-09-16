package org.jahdoo.capabilities.player_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.capabilities.AbstractCapability;
import org.jahdoo.networking.packet.MageFlightPacketS2CPacket;
import org.jahdoo.utils.GeneralHelpers;

public class MageFlight implements AbstractCapability {

    public int jumpTickCounter;
    public boolean lastJumped;
    public boolean isFlying;
    public boolean chargeMana;

    @Override
    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("jumpTickCounter", jumpTickCounter);
        nbt.putBoolean("lastJumped", lastJumped);
        nbt.putBoolean("isFlying", isFlying);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        jumpTickCounter = nbt.getInt("jumpTickCounter");
        lastJumped = nbt.getBoolean("lastJumped");
        isFlying = nbt.getBoolean("isFlying");
    }

    @Override
    public void copyFrom(AbstractCapability source) {
        if(source instanceof MageFlight mageFlight){
            this.isFlying = mageFlight.isFlying;
            this.lastJumped = mageFlight.lastJumped;
            this.jumpTickCounter = mageFlight.jumpTickCounter;
        }
    }

    public void serverFlight(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new MageFlightPacketS2CPacket(this.jumpTickCounter, this.isFlying, this.lastJumped));
        if(this.chargeMana){
            GeneralHelpers.chargeManaCost(0.2, player);
        }
    }

    public int getJumpTickCounter(){
        return this.jumpTickCounter;
    }

    public boolean getLastJumped(){
        return this.lastJumped;
    }

    public boolean getIsFlying(){
        return this.isFlying;
    }

    public void setJumpTickCounter(int jumpTickCounter){
        this.jumpTickCounter = jumpTickCounter;
    }

    public void setLastJumped(boolean lastJumped){
        this.lastJumped = lastJumped;
    }

    public void setFlying(boolean isFlying){
        this.isFlying = isFlying;
    }

    public void setChargeMana(boolean chargeMana){
        this.chargeMana = chargeMana;
    }

    private void playMageFlightSound(Player player){
        if (player.tickCount % 3 == 0) {
            GeneralHelpers.getSoundWithPosition(
                player.level(),
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                0.03f,
                (float) player.getDeltaMovement().y
            );
        }
    }

}
