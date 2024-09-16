package org.jahdoo.networking.packet;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.GeneralHelpers;

public class MageFlightPacketS2CPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MageFlightPacketS2CPacket> TYPE = new CustomPacketPayload.Type<>(GeneralHelpers.modResourceLocation("mage_flight_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MageFlightPacketS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(MageFlightPacketS2CPacket::toBytes, MageFlightPacketS2CPacket::new);

    private final int jumpTickCounter;
    private final boolean setFlying;
    private final boolean setLastJumped;

    public MageFlightPacketS2CPacket(int jumpTickCounter, boolean setFlying, boolean setLastJumped) {
        this.jumpTickCounter = jumpTickCounter;
        this.setFlying = setFlying;
        this.setLastJumped = setLastJumped;
    }


    public MageFlightPacketS2CPacket(FriendlyByteBuf buf) {
        this.jumpTickCounter = buf.readInt();
        this.setFlying = buf.readBoolean();
        this.setLastJumped = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeInt(this.jumpTickCounter);
        bug.writeBoolean(this.setFlying);
        bug.writeBoolean(this.setLastJumped);
    }

    public boolean handle(IPayloadContext ctx) {
       ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx instanceof LocalPlayer localPlayer) {
//                        localPlayer.getCapability(MageFlightProvider.GET_MAGE_FLIGHT).ifPresent(
//                            mageFlight -> {
//                                LocalPlayer player = Minecraft.getInstance().player;
//
//                                ItemStack wandItem = player.getMainHandItem();
//                                if(!(wandItem.getItem() instanceof WandItem)) return;
//                                ManaSystem manaSystem = CapabilityHelpers.getManaSystem(player);
//
//                                if (player.onGround()) mageFlight.isFlying = false;
//
//                                if (!mageFlight.lastJumped && player.input.jumping) {
//                                    if (mageFlight.jumpTickCounter == 0) mageFlight.jumpTickCounter = 10; else mageFlight.isFlying = true;
//                                }
//
//                                if (mageFlight.jumpTickCounter > 0) mageFlight.jumpTickCounter--;
//
//                                mageFlight.lastJumped = player.input.jumping;
//
//                                if(mageFlight.isFlying && player.input.jumping) {
//                                    mageFlight.chargeMana = true;
//                                    if (manaSystem.getManaPool() > 0.2) {
//
//                                        player.setDeltaMovement(player.getDeltaMovement().add(0, 0.05, 0));
//
//                                        AbstractElement element = getElementByWandType(wandItem.getItem()).get(0);
//                                        boolean getMovement = player.getDeltaMovement().y > -0.5;
//
//                                        GenericParticleOptions part1 = genericParticleOptions(
//                                            ParticleStore.GENERIC_PARTICLE_SELECTION, element, 2, 0.2f, true
//                                        );
//
//                                        BakedParticleOptions part2 = new BakedParticleOptions(
//                                            ElementRegistry.getElementByWandType(wandItem.getItem()).get(0).getTypeId(),
//                                            2, 1f, false
//                                        );
//
//                                        GeneralHelpers.getInnerRingOfRadiusRandom(player.position(), player.getBbWidth() - 0.3, getMovement ? 5 : 2,
//                                            positions -> {
//                                                player.level().addParticle(part1, positions.x, positions.y, positions.z, 0, -0.2, 0);
//                                                player.level().addParticle(part2, positions.x, positions.y, positions.z, 0, -0.2, 0);
//                                            }
//                                        );
//
//                                    }
//                                } else {
//                                    mageFlight.chargeMana = false;
//                                }
//                                Network.sendToServer(new FlyingPacketC2SPacket(mageFlight.chargeMana, mageFlight.lastJumped, mageFlight.isFlying, mageFlight.jumpTickCounter));
//                            }
//                        );
                    }
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
