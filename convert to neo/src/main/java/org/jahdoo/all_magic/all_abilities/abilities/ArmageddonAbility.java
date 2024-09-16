package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class ArmageddonAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("armageddon");
    public static final String aoe = "Area of Effect";
    public static final String speed = "Spawning Speed";

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 3f, ProjectilePropertyRegister.ARMAGEDDON.get().setAbilityId());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(80, 40, 1);
        makeTag.setCooldown(2400, 800, 100);
        makeTag.setDamage(25, 10, 1);
        makeTag.setCastingDistance(30, 10, 5);
        makeTag.setLifetime(500,300, 10);
        makeTag.setAbilityTagModifiersRandom(aoe, 6,1, true, 1);
        makeTag.setAbilityTagModifiersRandom(speed, 30,5, false, 5);
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
        return ElementRegistry.INFERNO.get();
    }
}
