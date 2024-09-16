//package org.jahdoo.capabilities.player_abilities;
//
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.Entity;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.CapabilityManager;
//import net.minecraftforge.common.capabilities.CapabilityToken;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.event.entity.EntityJoinLevelEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent;
//import net.minecraftforge.fml.LogicalSide;
//import org.jahdoo.capabilities.AbstractCapability;
//import org.jahdoo.capabilities.AbstractProvider;
//import org.jahdoo.networking.Network;
//import org.jahdoo.networking.packet.MageFlightPacketS2CPacket;
//import org.jahdoo.utils.GeneralHelpers;
//import org.jetbrains.annotations.NotNull;
//
//public class MageFlightProvider extends AbstractProvider {
//
//    public static @NotNull Capability<MageFlight> GET_MAGE_FLIGHT = CapabilityManager.get(new CapabilityToken<MageFlight>() {});
//    public static ResourceLocation CAPABILITY_LOCATION = GeneralHelpers.modResourceLocation("jahdoo_mage_flight");
//
//    @Override
//    public Capability<MageFlight> getToken() {
//        return GET_MAGE_FLIGHT;
//    }
//
//    @Override
//    protected AbstractCapability configuredAbility() {
//        return new MageFlight();
//    }
//
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        event.player.getCapability(GET_MAGE_FLIGHT).ifPresent(
//            mageFlight -> {
//                if(event.side == LogicalSide.SERVER){
//                    mageFlight.serverFlight((ServerPlayer) event.player);
//                }
//            }
//        );
//    }
//
//    public static void onPlayerAttach(AttachCapabilitiesEvent<Entity> event){
//        onAttachCapabilities(event, new MageFlightProvider(), GET_MAGE_FLIGHT, CAPABILITY_LOCATION);
//    }
//
//    public static void onPlayerCloned(PlayerEvent.Clone event){
//        onPlayerClonedEvent(event, GET_MAGE_FLIGHT);
//    }
//
//
//    public static void onPlayerJoin(EntityJoinLevelEvent event){
//        onPlayerJoinEvent(event, serverPlayer -> {
//                serverPlayer.getCapability(MageFlightProvider.GET_MAGE_FLIGHT).ifPresent(
//                    mageFlight -> Network.sendToClient(
//                        new MageFlightPacketS2CPacket(
//                            mageFlight.getJumpTickCounter(),
//                            mageFlight.getIsFlying(),
//                            mageFlight.getLastJumped()
//                        ),
//                        serverPlayer
//                    )
//                );
//            }
//        );
//    }
//
//}
