package org.jahdoo.registers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractEntityProperty;
import org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.*;
import org.jahdoo.all_magic.all_abilities.ability_components.EtherealArrow;
import org.jahdoo.all_magic.all_abilities.ability_components.LightningTrail;
import org.jahdoo.all_magic.all_abilities.utility.raw_utilities.*;

import java.util.function.Supplier;

public class ProjectilePropertyRegister {

    public static final ResourceKey<Registry<AbstractEntityProperty>> PROJECTILE_PROPERTY_REGISTRY_KEY =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID,"projectile_properties"));

    private static final DeferredRegister<AbstractEntityProperty> PROJECTILE_PROPERTY = DeferredRegister.create(PROJECTILE_PROPERTY_REGISTRY_KEY, JahdooMod.MOD_ID);

    public static final Registry<AbstractEntityProperty> REGISTRY =  new RegistryBuilder<>(PROJECTILE_PROPERTY_REGISTRY_KEY).create();

    private static Supplier<AbstractEntityProperty> registerProjectileProperty(AbstractEntityProperty spell) {
        return PROJECTILE_PROPERTY.register(spell.setAbilityId(), () -> spell);
    }

    //USED FOR ELEMENT PROJECTILE
    public static final Supplier <AbstractEntityProperty> FIRE_BALL = registerProjectileProperty(new FireBall());
    public static final Supplier <AbstractEntityProperty> FROST_BOLT = registerProjectileProperty(new FrostBolts());
    public static final Supplier <AbstractEntityProperty> ICE_NEEDLER = registerProjectileProperty(new IceBomb());
    public static final Supplier <AbstractEntityProperty> MYSTICAL_SEMTEX = registerProjectileProperty(new MysticalSemtex());
    public static final Supplier <AbstractEntityProperty> QUANTUM_DESTROYER = registerProjectileProperty(new QuantumDestroyer());
    public static final Supplier <AbstractEntityProperty> OVERCHARGED = registerProjectileProperty(new Overcharge());
    public static final Supplier <AbstractEntityProperty> BOLTZ = registerProjectileProperty(new Boltz());

    //USED FOR GENERIC PROJECTILE
    public static final Supplier <AbstractEntityProperty> ELEMENTAL_SHOOTER = registerProjectileProperty(new ElementalShooter());
    public static final Supplier <AbstractEntityProperty> LIGHTNING_TRAIL = registerProjectileProperty(new LightningTrail());
    public static final Supplier <AbstractEntityProperty> BLOCK_BREAKER = registerProjectileProperty(new BlockBreaker());
    public static final Supplier <AbstractEntityProperty> BLOCK_EXPLODER = registerProjectileProperty(new BlockExplosion());
    public static final Supplier <AbstractEntityProperty> LIGHT_PLACER = registerProjectileProperty(new LightPlacer());
    public static final Supplier <AbstractEntityProperty> VEIN_MINER = registerProjectileProperty(new VeinMiner());
    public static final Supplier <AbstractEntityProperty> BLOCK_PLACER = registerProjectileProperty(new BlockPlacer());
    public static final Supplier <AbstractEntityProperty> HAMMER = registerProjectileProperty(new Hammer());
    public static final Supplier <AbstractEntityProperty> BONE_MEAL = registerProjectileProperty(new BoneMeal());
    public static final Supplier <AbstractEntityProperty> ETHEREAL_ARROW = registerProjectileProperty(new EtherealArrow());

    //USED FOR AOE ENTITY
    public static final Supplier <AbstractEntityProperty> ARCTIC_STORM = registerProjectileProperty(new ArcticStorm());
    public static final Supplier <AbstractEntityProperty> ARMAGEDDON_MODULE = registerProjectileProperty(new ArmageddonModule());
    public static final Supplier <AbstractEntityProperty> ARMAGEDDON = registerProjectileProperty(new Armageddon());
    public static final Supplier <AbstractEntityProperty> HELLFIRE = registerProjectileProperty(new HellFire());
    public static final Supplier <AbstractEntityProperty> SUMMON_ETERNAL_WIZARD = registerProjectileProperty(new SummonEternalWizard());

    public static void register(IEventBus eventBus) {
        PROJECTILE_PROPERTY.register(eventBus);
    }
}
