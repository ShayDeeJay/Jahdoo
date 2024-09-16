package org.jahdoo.registers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.all_abilities.abilities.*;
import org.jahdoo.all_magic.all_abilities.utility.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AbilityRegister {

    public static final ResourceKey<Registry<AbstractAbility>> ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID,"all_magic"));
    private static final DeferredRegister<AbstractAbility> ABILITIES = DeferredRegister.create(ABILITY_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractAbility> REGISTRY =  new RegistryBuilder<>(ABILITY_REGISTRY_KEY).create();


    public static List<AbstractAbility> getSpellsByTypeId(String typeId) {
        return AbilityRegister.REGISTRY
            .stream()
            .filter(ability -> Objects.equals(ability.setAbilityId(), typeId))
            .toList();
    }


    public static Optional<AbstractAbility> getFirstSpellByTypeId(String typeId) {
        return AbilityRegister.REGISTRY
            .stream()
            .filter(ability -> Objects.equals(ability.setAbilityId(), typeId))
            .findFirst();  // Lazy and returns an Optional
    }

    private static DeferredHolder<AbstractAbility, AbstractAbility> registerSpell(AbstractAbility spell) {
        return ABILITIES.register(spell.setAbilityId(), () -> spell);
    }

    //Multi-Type
    public static final DeferredHolder<AbstractAbility, AbstractAbility> ELEMENTAL_SHOOTER = registerSpell(new ElementalShooterAbility());

    //Volt
    public static final DeferredHolder<AbstractAbility, AbstractAbility> BOLTZ = registerSpell(new BoltzAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> STATIC = registerSpell(new StaticAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> STORM_RUSH = registerSpell(new StormRushAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> OVERCHARGED = registerSpell(new OverchargedAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> THUNDER_BURST = registerSpell(new ThunderBurstAbility());

    //Inferno
    public static final DeferredHolder<AbstractAbility, AbstractAbility> ARMAGEDDON = registerSpell(new ArmageddonAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> FIREBALL = registerSpell(new FireballAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> HELLFIRE = registerSpell(new HellfireAbility());

    //Mystic
    public static final DeferredHolder<AbstractAbility, AbstractAbility> ARCANE_SHIFT = registerSpell(new ArcaneShiftAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> MYSTICAL_SEMTEX = registerSpell(new MysticalSemtexAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> QUANTUM_DESTROYER = registerSpell(new QuantumDestroyerAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> NOVA_SMASH = registerSpell(new NovaSmashAbility());

    //Frost
    public static final DeferredHolder<AbstractAbility, AbstractAbility> ARCTIC_STORM = registerSpell(new ArcticStormAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> ICE_NEEDLER = registerSpell(new IceBombAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> FROST_BOLTS = registerSpell(new FrostboltsAbility());

    //Vitality
    public static final DeferredHolder<AbstractAbility, AbstractAbility> SUMMON_ETERNAL_WIZARD = registerSpell(new SummonEternalWizardAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> ESCAPE_DECOY = registerSpell(new EscapeDecoy());

    //Utility
    public static final DeferredHolder<AbstractAbility, AbstractAbility> BLOCK_BOMB = registerSpell(new BlockBombAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> BLOCK_BREAKER = registerSpell(new BlockBreakerAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> BLOCK_PLACER = registerSpell(new BlockPlacerAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> BONE_MEAL = registerSpell(new BoneMealAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> HAMMER = registerSpell(new HammerAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> LIGHT_PLACER = registerSpell(new LightPlacerAbility());
    public static final DeferredHolder<AbstractAbility, AbstractAbility> VEIN_MINER = registerSpell(new VeinMinerAbility());

    public static void register(IEventBus eventBus) {
        ABILITIES.register(eventBus);
    }

}
