package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class StaticAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("static");
    public static final String mana_per_damage = "Mana Per Hit";

    @Override
    public void invokeAbility(Player player) {
//        player.getCapability(StaticProvider.STATIC_ABILITY).ifPresent(
//            aStatic -> {
//                if(aStatic.getIsActive()) aStatic.deactivate(player); else aStatic.activate(player);
//            }
//        );
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(50, 20,  1);
        makeTag.setCooldown(1200, 600, 50);
        makeTag.setDamage(15, 5, 1);
        makeTag.setEffectDuration(300, 20, 20);
        makeTag.setEffectStrength(10, 1,1);
        makeTag.setEffectChance(30,1,1);
        makeTag.setRange(10,1,1);
        makeTag.setAbilityTagModifiersRandom(mana_per_damage, 10,5, true, 1);
        makeTag.setModifiers(itemStack, abilityId.getPath().intern());
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return AREA_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public boolean internallyChargeManaAndCooldown() {
        return true;
    }

    @Override
    public boolean isSwitchAbility() {
        return true ;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.LIGHTNING.get();
    }
}
