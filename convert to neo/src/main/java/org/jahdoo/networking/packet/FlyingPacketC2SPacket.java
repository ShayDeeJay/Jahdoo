package org.jahdoo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.GeneralHelpers;

public class FlyingPacketC2SPacket implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<FlyingPacketC2SPacket> TYPE = new CustomPacketPayload.Type<>(GeneralHelpers.modResourceLocation("send_flying_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FlyingPacketC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(FlyingPacketC2SPacket::toBytes, FlyingPacketC2SPacket::new);
    private final boolean chargeMana;
    private boolean lastJumped;
    private boolean isFlying;
    private int jumpTickCounter;

    public FlyingPacketC2SPacket(boolean chargeMana, boolean lastJumped, boolean isFlying, int jumpTickCounter) {
        this.chargeMana = chargeMana;
        this.lastJumped = lastJumped;
        this.isFlying = isFlying;
        this.jumpTickCounter = jumpTickCounter;
    }

    public FlyingPacketC2SPacket(FriendlyByteBuf buf) {
        this.chargeMana = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeBoolean(this.chargeMana);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    serverPlayer.getAbilities().mayfly = true;
                    if (serverPlayer.onGround()) serverPlayer.getAbilities().mayfly = false;
//                    serverPlayer.getCapability(MageFlightProvider.GET_MAGE_FLIGHT).ifPresent(
//                        mageFlight -> {
//                            mageFlight.setChargeMana(this.chargeMana);
//                            mageFlight.setLastJumped(this.lastJumped);
//                            mageFlight.setFlying(this.isFlying);
//                            mageFlight.setJumpTickCounter(this.jumpTickCounter);
//                        }
//                    );
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
