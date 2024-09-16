//package org.jahdoo.capabilities.player_abilities;
//
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.Entity;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.CapabilityManager;
//import net.minecraftforge.common.capabilities.CapabilityToken;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.event.entity.EntityJoinLevelEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent;
//import org.jahdoo.capabilities.AbstractCapability;
//import org.jahdoo.capabilities.AbstractProvider;
//import org.jahdoo.networking.Network;
//import org.jahdoo.networking.packet.NovaSmashS2CPacket;
//import org.jahdoo.utils.GeneralHelpers;
//import org.jetbrains.annotations.NotNull;
//
//public class NovaSmashAbility extends AbstractProvider {
//
//    public static @NotNull Capability<NovaSmash> GET_NOVA_SMASH = CapabilityManager.get(new CapabilityToken<NovaSmash>() {});
//    public static ResourceLocation CAPABILITY_LOCATION = GeneralHelpers.modResourceLocation("jahdoo_nova_smash");
//
//    @Override
//    public Capability<NovaSmash> getToken() {
//        return GET_NOVA_SMASH;
//    }
//
//    @Override
//    protected AbstractCapability configuredAbility() {
//        return new NovaSmash();
//    }
//
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        event.player.getCapability(GET_NOVA_SMASH).ifPresent(
//            novaSmashAbility -> novaSmashAbility.setPlayerSmash(event.player)
//        );
//    }
//
//    public static void onPlayerAttach(AttachCapabilitiesEvent<Entity> event){
//        onAttachCapabilities(event, new NovaSmashAbility(), GET_NOVA_SMASH, CAPABILITY_LOCATION);
//    }
//
//    public static void onPlayerCloned(PlayerEvent.Clone event){
//        onPlayerClonedEvent(event, GET_NOVA_SMASH);
//    }
//
//
//    public static void onPlayerJoin(EntityJoinLevelEvent event){
//        onPlayerJoinEvent(event, serverPlayer -> {
//                serverPlayer.getCapability(NovaSmashAbility.GET_NOVA_SMASH).ifPresent(
//                    novaSmashAbility -> Network.sendToClient(new NovaSmashS2CPacket(novaSmashAbility.getSetHighestDelta(), novaSmashAbility.getCanSmash()), serverPlayer)
//                );
//            }
//        );
//    }
//
//}
