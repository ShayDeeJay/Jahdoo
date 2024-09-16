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

public class MysticalSemtexAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("mystical_semtex");
    public static final String arcs = "Projectile Arc";
    public static final String additionalProjectile = "Additional Projectiles";
    public static final String explosionDelays = "Explosion Delay";
    public static final String additionalProjectileChance = "Additional Projectile Chance";
    public static final String explosionRadius = "Explosion Radius";

    @Override
    public void invokeAbility(Player player) {
        fireProjectile(new ElementProjectile(EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(), player, ProjectilePropertyRegister.MYSTICAL_SEMTEX.get().setAbilityId(),0), player, 0.8f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(20, 10,  1);
        makeTag.setCooldown(400, 100, 20);
        makeTag.setDamage(35, 15, 5);
        makeTag.setAbilityTagModifiersRandom(arcs, 0.03,0.01, false, 0.01);
        makeTag.setAbilityTagModifiersRandom(additionalProjectile, 8,2, true, 1);
        makeTag.setAbilityTagModifiersRandom(explosionDelays, 50,20, true, 5);
        makeTag.setAbilityTagModifiersRandom(additionalProjectileChance, 10,1, true, 1);
        makeTag.setAbilityTagModifiersRandom(explosionRadius, 8,3, true, 1);
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
        return ElementRegistry.MYSTIC.get();
    }
}
