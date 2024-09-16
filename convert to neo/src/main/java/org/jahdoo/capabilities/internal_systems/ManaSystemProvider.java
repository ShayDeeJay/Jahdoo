//package org.jahdoo.capabilities.internal_systems;
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
//import org.jahdoo.networking.packet.ManaDataSyncS2CPacket;
//import org.jahdoo.utils.GeneralHelpers;
//
//public class ManaSystemProvider extends AbstractProvider {
//
//    public static Capability<ManaSystem> GET_MANA = CapabilityManager.get(new CapabilityToken<ManaSystem>() {});
//    public static ResourceLocation CAPABILITY_LOCATION = GeneralHelpers.modResourceLocation("jahdoo_mana_properties");
//
//    @Override
//    public Capability<ManaSystem> getToken() {
//        return GET_MANA;
//    }
//
//    @Override
//    protected AbstractCapability configuredAbility() {
//        return new ManaSystem();
//    }
//
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        if(event.side == LogicalSide.SERVER){
//            event.player.getCapability(ManaSystemProvider.GET_MANA).ifPresent(
//                jahdooMana -> {
//                    if (jahdooMana.getManaPool() < jahdooMana.getMaxMana()) {
//                        jahdooMana.onTick(event.player);
//                        Network.sendToClient(new ManaDataSyncS2CPacket(jahdooMana.getManaPool()), (ServerPlayer) event.player);
//                    }
//                }
//            );
//        }
//    }
//
//    public static void onPlayerAttach(AttachCapabilitiesEvent<Entity> event){
//        onAttachCapabilities(event, new ManaSystemProvider(), GET_MANA, CAPABILITY_LOCATION);
//    }
//
//    public static void onPlayerCloned(PlayerEvent.Clone event){
//        onPlayerClonedEvent(event, GET_MANA);
//    }
//
//    public static void onPlayerJoin(EntityJoinLevelEvent event){
//        onPlayerJoinEvent(event, serverPlayer -> {
//                serverPlayer.getCapability(ManaSystemProvider.GET_MANA).ifPresent(
//                    jahdooMana -> Network.sendToClient(new ManaDataSyncS2CPacket(jahdooMana.getManaPool()), serverPlayer)
//                );
//            }
//        );
//    }
//
//}
