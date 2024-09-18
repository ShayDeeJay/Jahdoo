package org.jahdoo.registers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jahdoo.JahdooMod;
import org.jahdoo.capabilities.CastingData;
import org.jahdoo.capabilities.CastingDataProvider;
import org.jahdoo.capabilities.player_abilities.*;

public class AttachmentRegister {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JahdooMod.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CastingData>> CASTER_DATA = ATTACHMENT_TYPES.register(
        "caster_data", () -> AttachmentType.builder((holder) -> holder instanceof ServerPlayer serverPlayer ? new CastingData(serverPlayer) : new CastingData()).serialize(new CastingDataProvider()).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MageFlight>> MAGE_FLIGHT = ATTACHMENT_TYPES.register(
        "mage_flight", () -> AttachmentType.builder((holder) -> new MageFlight()).serialize(new MageFlightProvider()).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NovaSmash>> NOVA_SMASH = ATTACHMENT_TYPES.register(
        "nova_smash", () -> AttachmentType.builder((holder) -> new NovaSmash()).serialize(new NovaSmashProvider()).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Static>> STATIC = ATTACHMENT_TYPES.register(
        "static", () -> AttachmentType.builder((holder) -> new Static()).serialize(new StaticProvider()).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
