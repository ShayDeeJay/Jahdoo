package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class IceBombAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("ice_bomb");

    @Override
    public void invokeAbility(Player player) {
        fireProjectile(
            new ElementProjectile(
                EntitiesRegister.FROST_ELEMENT_PROJECTILE.get(),
                player,
                ProjectilePropertyRegister.ICE_NEEDLER.get().setAbilityId(),
                0
            ), player, 0.5f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(30, 10,  1);
        makeTag.setCooldown(600, 200, 50);
        makeTag.setDamage(15, 5, 1);
        makeTag.setEffectStrength(10, 5,1);
        makeTag.setEffectDuration(400,200,10);
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
        return ElementRegistry.FROST.get();
    }
}
