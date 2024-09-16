package org.jahdoo.networking.packet;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.GeneralHelpers;

import java.util.Map;

public class CooldownsDataSyncS2CPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CooldownsDataSyncS2CPacket> TYPE = new CustomPacketPayload.Type<>(GeneralHelpers.modResourceLocation("player_cooldowns"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CooldownsDataSyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(CooldownsDataSyncS2CPacket::toBytes, CooldownsDataSyncS2CPacket::new);

    private final Map<String, Integer> abilityCooldowns;

    public CooldownsDataSyncS2CPacket(Map<String, Integer> abilityCooldowns) {
        this.abilityCooldowns = abilityCooldowns;
    }

    public CooldownsDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.abilityCooldowns = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(this.abilityCooldowns, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof LocalPlayer localPlayer) {
//                        localPlayer.getCapability(AbilityCooldownSystemProvider.GET_COOLDOWNS);
                    }

                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
