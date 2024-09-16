package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class QuantumDestroyerAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("quantum_destroyer");
    public static final String radius = "Energy Radius";
    public static final String gravitationalPull = "Gravitational Pull";

    @Override
    public void invokeAbility(Player player) {
        ElementProjectile elementProjectile = new ElementProjectile(EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(), player, ProjectilePropertyRegister.QUANTUM_DESTROYER.get().setAbilityId(), 0);
        Vec3 position = player.pick(20, 0, false).getLocation();
        elementProjectile.moveTo(position.x, position.y + 0.3, position.z);
        fireProjectile(elementProjectile, player, 0.0f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(80, 40,  1);
        makeTag.setCooldown(2400, 800, 100);
        makeTag.setDamage(10, 3, 1);
        makeTag.setCastingDistance(30,10,5);
        makeTag.setLifetime(400, 200, 10);
        makeTag.setAbilityTagModifiersRandom(radius, 6,3, true, 1);
        makeTag.setAbilityTagModifiersRandom(gravitationalPull, 1.5,0.5, true, 0.1);
        makeTag.setModifiers(itemStack, abilityId.getPath().intern());
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return DISTANCE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.MYSTIC.get();
    }
}
