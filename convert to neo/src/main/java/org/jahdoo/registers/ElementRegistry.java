package org.jahdoo.registers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.elements.*;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;

public class ElementRegistry {

    public static final ResourceKey<Registry<AbstractElement>> ELEMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(GeneralHelpers.modResourceLocation("element"));
    private static final DeferredRegister<AbstractElement> ELEMENT = DeferredRegister.create(ELEMENT_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractElement> REGISTRY =  new RegistryBuilder<>(ELEMENT_REGISTRY_KEY).create();

    public static List<AbstractElement> getElementByWandType(Item wand) {
        return ElementRegistry.REGISTRY
            .stream()
            .filter(ability -> ability.getWand() == wand)
            .toList();
    }

    public static List<AbstractElement> getElementByTypeId(int typeId) {
        return ElementRegistry.REGISTRY
            .stream()
            .filter(ability -> ability.getTypeId() == typeId)
            .toList();
    }

    private static DeferredHolder<AbstractElement, AbstractElement> registerElement(AbstractElement spell) {
        return ELEMENT.register(spell.setAbilityId(), () -> spell);
    }

    public static final DeferredHolder<AbstractElement, AbstractElement> INFERNO = registerElement(new Inferno());
    public static final DeferredHolder<AbstractElement, AbstractElement> FROST = registerElement(new Frost());
    public static final DeferredHolder<AbstractElement, AbstractElement> LIGHTNING = registerElement(new Lightning());
    public static final DeferredHolder<AbstractElement, AbstractElement> MYSTIC = registerElement(new Mystic());
    public static final DeferredHolder<AbstractElement, AbstractElement> VITALITY = registerElement(new Vitality());
    public static final DeferredHolder<AbstractElement, AbstractElement> UTILITY = registerElement(new Utility());


    public static void register(IEventBus eventBus) {
        ELEMENT.register(eventBus);
    }

}
