package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.effects.custom_effects.*;
import org.jahdoo.all_magic.effects.custom_effects.type_effects.*;

public class EffectsRegister {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
        DeferredRegister.create(Registries.MOB_EFFECT, JahdooMod.MOD_ID);
//
//    public static final RegistryObject<MobEffect> STEP_BOOST = MOB_EFFECTS.register("step_boost",
//        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3434234).addAttributeModifier(ForgeMod.AND_HOLDER_SET.get(), "75ab037d-34d9-4cb9-a918-b11b454eb738",1.0D, AttributeModifier.Operation.ADD_VALUE ));
//
//    public static final RegistryObject<MobEffect> AMPLIFY_BLOCK_REACH = MOB_EFFECTS.register("amplify_block_reach",
//        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3436524).addAttributeModifier(ForgeMod.AND_HOLDER_SET.get(), "2e121062-10de-4f66-ba73-91a43f4325cb",1.0D, AttributeModifier.Operation.ADDITION ));
//
//    public static final RegistryObject<MobEffect> AMPLIFY_ENTITY_REACH = MOB_EFFECTS.register("amplify_entity_reach",
//        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3436524).addAttributeModifier(ForgeMod.AND_HOLDER_SET.get(), "2e121062-10de-4f66-ba73-91a43f4325cb",1.0D, AttributeModifier.Operation.ADDITION ));

    public static final DeferredHolder<MobEffect, MobEffect> NO_FALL_DAMAGE= MOB_EFFECTS.register("no_fall_damage", () -> new FallDamageEffect(MobEffectCategory.BENEFICIAL, 3436524));

    public static final DeferredHolder<MobEffect, MobEffect> ITEM_MAGNET = MOB_EFFECTS.register("item_magnet",
        () -> new ItemMagnetEffect(MobEffectCategory.BENEFICIAL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> MAGIC_AMPLIFIER = MOB_EFFECTS.register("magic_amplifier",
        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> LUCKY_ROLLER = MOB_EFFECTS.register("lucky_roller",
        () -> new GenericEffect(MobEffectCategory.BENEFICIAL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> STUN_EFFECT = MOB_EFFECTS.register("stun_effect",
        () -> new StunEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> ICE_EFFECT = MOB_EFFECTS.register("ice_effect",
        () -> new IceEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> ARCANE_EFFECT = MOB_EFFECTS.register("mystic_damage",
        () -> new ArcaneEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> FIRE_EFFECT = MOB_EFFECTS.register("fire_damage",
        () -> new FireEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> LIGHTNING_EFFECT = MOB_EFFECTS.register("lightning_effect",
        () -> new LightningEffect(MobEffectCategory.HARMFUL, 3436524)
    );
    public static final DeferredHolder<MobEffect, MobEffect> VITALITY_EFFECT = MOB_EFFECTS.register("vitality_effect",
        () -> new VitalityEffect(MobEffectCategory.HARMFUL, 3436524)
    );


    public static void register (IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }

}
