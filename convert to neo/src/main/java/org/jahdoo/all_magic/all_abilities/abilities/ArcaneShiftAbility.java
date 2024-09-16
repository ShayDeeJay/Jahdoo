package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.ArcaneShift;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class ArcaneShiftAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("arcane_shift");
    public static final String distance = "Teleport Distance";
    public static final String maxEntities = "Mystic Missile Shots";
    public static final String lifeTime = "Shot Range";

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

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(50, 20, 1);
        makeTag.setCooldown(1200, 400, 100);
        makeTag.setDamage(20, 5, 1);
        makeTag.setAbilityTagModifiersRandom(distance, 50,25, true, 5);
        makeTag.setAbilityTagModifiersRandom(maxEntities, 30,5, true, 5);
        makeTag.setAbilityTagModifiersRandom(lifeTime, 10,5, true, 1);
        makeTag.setModifiers(itemStack, abilityId.getPath().intern());
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public void invokeAbility(Player player) {
        new ArcaneShift(player).teleportToHome();
    }

}
