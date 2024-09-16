//package org.jahdoo.capabilities;
//
//import net.minecraft.core.Direction;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.common.capabilities.AutoRegisterCapability;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
//import net.minecraftforge.event.entity.EntityJoinLevelEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent;
//import net.neoforged.neoforge.capabilities.ICapabilityProvider;
//import org.jahdoo.capabilities.player_abilities.MageFlightProvider;
//import org.jetbrains.annotations.NotNull;
//
//import javax.annotation.Nullable;
//import java.util.function.Consumer;
//@AutoRegisterCapability
//public abstract class AbstractProvider implements ICapabilityProvider {
//    private AbstractCapability abstractPlayerAbility = null;
//    public abstract <T extends AbstractCapability> Capability<T> getToken();
//    protected abstract AbstractCapability configuredAbility();
//
//    protected final LazyOptional<AbstractCapability> optional = LazyOptional.of(this::createIfNotPresent);
//
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if(cap == getToken()) return this.optional.cast();
//        return LazyOptional.empty();
//    }
//
//
//    protected AbstractCapability createIfNotPresent() {
//        if(this.abstractPlayerAbility == null) this.abstractPlayerAbility = configuredAbility();
//        return this.abstractPlayerAbility;
//    }
//
//
//
//    protected static <AbstractPlayerAbility> void onAttachCapabilities(
//        AttachCapabilitiesEvent<Entity> event,
//        ICapabilityProvider capabilityProvider,
//        Capability<AbstractPlayerAbility> cap,
//        ResourceLocation resourceLocation
//    ) {
//        if (event.getObject() instanceof Player player) {
//            if (!player.getCapability(cap).isPresent()) {
//                event.addCapability(resourceLocation, capabilityProvider);
//            }
//        }
//    }
//
//    protected static <AbstractPlayerAbility> void onPlayerClonedEvent(PlayerEvent.Clone event, Capability<AbstractPlayerAbility> cap) {
//        if(event.isWasDeath()) {
//            event.getOriginal()
//                .getCapability(cap)
//                .ifPresent(
//                    oldStore ->{
//                        event.getOriginal().getCapability(MageFlightProvider.GET_MAGE_FLIGHT).ifPresent(
//                            newStore -> newStore.copyFrom((AbstractCapability) oldStore)
//                        );
//                    }
//                );
//        }
//    }
//
//
//    protected static void onPlayerJoinEvent(EntityJoinLevelEvent event, Consumer<ServerPlayer> execute){
//        if(!event.getLevel().isClientSide){
//            if(event.getEntity() instanceof ServerPlayer serverPlayer){
//                execute.accept(serverPlayer);
//            }
//        }
//    }
//
//
//}
