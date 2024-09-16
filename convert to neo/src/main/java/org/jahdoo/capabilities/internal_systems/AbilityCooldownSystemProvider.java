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
//import net.neoforged.neoforge.network.PacketDistributor;
//import org.jahdoo.capabilities.AbstractCapability;
//import org.jahdoo.capabilities.AbstractProvider;
//import org.jahdoo.networking.Network;
//import org.jahdoo.networking.packet.CooldownsDataSyncS2CPacket;
//import org.jahdoo.utils.GeneralHelpers;
//
//public class AbilityCooldownSystemProvider extends AbstractProvider {
//
//    public static Capability<AbilityCooldownSystem> GET_COOLDOWNS = CapabilityManager.get(new CapabilityToken<AbilityCooldownSystem>() {});
//    public static ResourceLocation CAPABILITY_LOCATION = GeneralHelpers.modResourceLocation("jahdoo_cooldown_properties");
//
//    @Override
//    public Capability<AbilityCooldownSystem> getToken() {
//        return GET_COOLDOWNS;
//    }
//
//    @Override
//    protected AbstractCapability configuredAbility() {
//        return new AbilityCooldownSystem();
//    }
//
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        if(event.side == LogicalSide.SERVER){
//            event.player.getCapability(AbilityCooldownSystemProvider.GET_COOLDOWNS).ifPresent(
//                abilityCooldownSystem -> {
//                    abilityCooldownSystem.applyAllCooldowns();
//                    PacketDistributor.sendToPlayer(new CooldownsDataSyncS2CPacket(abilityCooldownSystem.getAllCooldowns()), (ServerPlayer) event.player);
//                }
//            );
//        }
//    }
//
//    public static void onPlayerAttach(AttachCapabilitiesEvent<Entity> event){
//        onAttachCapabilities(event, new AbilityCooldownSystemProvider(), GET_COOLDOWNS, CAPABILITY_LOCATION);
//    }
//
//    public static void onPlayerCloned(PlayerEvent.Clone event){
//        onPlayerClonedEvent(event, GET_COOLDOWNS);
//    }
//
//    public static void onPlayerJoin(EntityJoinLevelEvent event){
//        onPlayerJoinEvent(event, serverPlayer -> {
//                serverPlayer.getCapability(AbilityCooldownSystemProvider.GET_COOLDOWNS).ifPresent(
//                    abilityCooldownSystem -> Network.sendToClient(new CooldownsDataSyncS2CPacket(abilityCooldownSystem.getAllCooldowns()), serverPlayer)
//                );
//            }
//        );
//    }
//
//}
