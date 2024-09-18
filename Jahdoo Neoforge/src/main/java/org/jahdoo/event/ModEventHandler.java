package org.jahdoo.event;

import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.BlockEntitiesRegister;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, BlockEntitiesRegister.TANK_BE.get(), (blockEntity, side) -> blockEntity.inputItemHandler
        );
    }


}
