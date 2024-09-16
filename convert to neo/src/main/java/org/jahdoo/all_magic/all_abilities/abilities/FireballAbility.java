package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.Components.AbilityHolder;
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

public class FireballAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("fireball");
    public static final String novaRange = "Explosion Radius";

    @Override
    public void invokeAbility(Player player) {
        ElementProjectile elementProjectile = new ElementProjectile(EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(), player, ProjectilePropertyRegister.FIRE_BALL.get().setAbilityId(), -0.0);
        this.fireProjectile(elementProjectile, player, 0.5f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(30, 10, 1);
        makeTag.setCooldown(600, 200, 100);
        makeTag.setDamage(25, 10, 1);
        makeTag.setEffectDuration(300, 100, 20);
        makeTag.setEffectStrength(10, 0,1);
        makeTag.setEffectChance(40, 10, 5);
        makeTag.setAbilityTagModifiersRandom(novaRange, 8,4, true, 1);
        makeTag.setModifiers(itemStack, abilityId.getPath().intern());
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return PROJECTILE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.INFERNO.get();
    }
}
