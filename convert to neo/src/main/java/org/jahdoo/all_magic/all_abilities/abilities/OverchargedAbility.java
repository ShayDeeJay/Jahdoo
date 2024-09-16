package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class OverchargedAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("overcharged");
    public static final String gravitationalPull = "Gravitational Pull";
    public static final String instability = "Instability";

    @Override
    public void invokeAbility(Player player) {
        fireProjectile(new ElementProjectile(EntitiesRegister.LIGHTNING_ELEMENT_PROJECTILE.get(), player, ProjectilePropertyRegister.OVERCHARGED.get().setAbilityId(), 0), player, 0.8f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(50, 20,  1);
        makeTag.setCooldown(1200, 300, 50);
        makeTag.setDamage(25, 10, 1);
        makeTag.setEffectDuration(300, 100, 20);
        makeTag.setEffectStrength(10, 0, 1);
        makeTag.setEffectChance(40, 5, 5);
        makeTag.setAbilityTagModifiersRandom(gravitationalPull, 1.4,0.2, true, 0.1);
        makeTag.setAbilityTagModifiersRandom(instability, 10,2, false, 1);
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
        return ElementRegistry.LIGHTNING.get();
    }
}
