package org.jahdoo;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jahdoo.loot.ModLootModifiers;
import org.jahdoo.recipe.ModRecipes;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ModCreativeModTabs;

@Mod(JahdooMod.MOD_ID)
public class JahdooMod {

    public static final String MOD_ID = "jahdoo";
    public static final Logger logger = LogManager.getLogger("jahdoo_mod");


    public JahdooMod(IEventBus modEventBus) {
//        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ModCreativeModTabs.register(modEventBus);
        BlocksRegister.register(modEventBus);
        BlockEntitiesRegister.register(modEventBus);
        MenusRegister.register(modEventBus);
        EntitiesRegister.register(modEventBus);
        EffectsRegister.register(modEventBus);
        ParticlesRegister.register(modEventBus);
        SoundRegister.register(modEventBus);
        ItemsRegister.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModRecipes.register(modEventBus);
        DataComponentRegistry.register(modEventBus);
        ProjectilePropertyRegister.register(modEventBus);
        AbilityRegister.register(modEventBus);
        ElementRegistry.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

//            MenuScreens.register(MenusRegister.CRYSTAL_INFUSION_MENU.get(), InfusionTableScreen::new);
//            MenuScreens.register(MenusRegister.WAND_BLOCK_MENU.get(), WandBlockScreen::new);

        }

    }

}
