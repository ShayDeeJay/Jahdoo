package org.jahdoo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.GeneralHelpers;

public class StopUsingC2SPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StopUsingC2SPacket> TYPE = new CustomPacketPayload.Type<>(GeneralHelpers.modResourceLocation("selected_ability"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StopUsingC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(StopUsingC2SPacket::toBytes, StopUsingC2SPacket::new);

    public StopUsingC2SPacket() {}

    public StopUsingC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork( () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.getMainHandItem().getItem() instanceof WandItem){
                    serverPlayer.releaseUsingItem();
                }
            }
        );
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
