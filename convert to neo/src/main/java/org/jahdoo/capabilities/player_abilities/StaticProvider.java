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
//import org.jahdoo.utils.GeneralHelpers;
//import org.jetbrains.annotations.NotNull;
//
//public class StaticProvider extends AbstractProvider {
//
//    public static @NotNull Capability<Static> STATIC_ABILITY = CapabilityManager.get(new CapabilityToken<Static>() {});
//    public static ResourceLocation CAPABILITY_LOCATION = GeneralHelpers.modResourceLocation("jahdoo_static");
//
//    @Override
//    public Capability<Static> getToken() {
//        return STATIC_ABILITY;
//    }
//
//    @Override
//    protected AbstractCapability configuredAbility() {
//        return new Static();
//    }
//
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        event.player.getCapability(STATIC_ABILITY).ifPresent(
//            staticAbility -> staticAbility.onTickMethod(event.player)
//        );
//    }
//
//    public static void onPlayerAttach(AttachCapabilitiesEvent<Entity> event){
//        onAttachCapabilities(event, new StaticProvider(), STATIC_ABILITY, CAPABILITY_LOCATION);
//    }
//
//    public static void onPlayerCloned(PlayerEvent.Clone event){
//        onPlayerClonedEvent(event, STATIC_ABILITY);
//    }
//
//
//    public static void onPlayerJoin(EntityJoinLevelEvent event){
//        onPlayerJoinEvent(event, serverPlayer -> {});
//    }
//
//
//}
