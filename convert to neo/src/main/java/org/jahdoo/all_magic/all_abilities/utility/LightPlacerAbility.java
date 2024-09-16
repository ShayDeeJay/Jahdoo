package org.jahdoo.all_magic.all_abilities.utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;


public class LightPlacerAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("light_placer");

    @Override
    public void invokeAbility(Player player) {

        GenericProjectile genericProjectile = new GenericProjectile(player, 0.06, ProjectilePropertyRegister.LIGHT_PLACER.get().setAbilityId());
        fireProjectile(genericProjectile, player, 1f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
//        TagModifierHelper makeTag = new TagModifierHelper(player);
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(10, 5,  1);
        makeTag.setCooldown(40, 10, 5);
        makeTag.setModifiers(itemStack, abilityId.getPath().intern());
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
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
        return ElementRegistry.UTILITY.get();
    }
}
