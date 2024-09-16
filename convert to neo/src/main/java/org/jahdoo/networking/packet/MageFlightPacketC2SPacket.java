//package org.jahdoo.networking.packet;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraftforge.event.network.CustomPayloadEvent;
//import org.jahdoo.capabilities.player_abilities.MageFlightProvider;
//
//import java.util.function.Supplier;
//
//public class MageFlightPacketC2SPacket {
//
//    private final boolean chargeMana;
//
//    public MageFlightPacketC2SPacket(boolean chargeMana) {
//        this.chargeMana = chargeMana;
//    }
//
//    public MageFlightPacketC2SPacket(FriendlyByteBuf buf) {
//        this.chargeMana = buf.readBoolean();
//    }
//
//    public void toBytes(FriendlyByteBuf bug) {
//        bug.writeBoolean(this.chargeMana);
//    }
//
//    public boolean handle(CustomPayloadEvent.Context ctx) {
//        ctx.enqueueWork(
//            () -> {
//                ServerPlayer serverPlayer = ctx.getSender();
//                if(serverPlayer != null){
//                    serverPlayer.getAbilities().mayfly = true;
//                    if (serverPlayer.onGround()) serverPlayer.getAbilities().mayfly = false;
//                    serverPlayer.getCapability(MageFlightProvider.GET_MAGE_FLIGHT).ifPresent(
//                        mageFlight -> {
//                            mageFlight.setChargeMana(this.chargeMana);
//                        }
//                    );
//                }
//            }
//        );
//        return true;
//    }
//
//}
