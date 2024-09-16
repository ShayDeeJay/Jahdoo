package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class ArcticStormAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("arctic_storm");
    public static final String size = "Effect Radius";
    public static final String trapDura = "Trap Duration";

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0f, ProjectilePropertyRegister.ARCTIC_STORM.get().setAbilityId());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.ICE_ATTACH.get(), SoundSource.BLOCKS, 1.2f, 0.6f);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.ORB_FIRE.get(), SoundSource.BLOCKS, 0.4f, 2f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(50, 20,  1);
        makeTag.setCooldown(1800, 1200, 100);
        makeTag.setDamage(10, 1, 1);
        makeTag.setEffectDuration(600, 200, 50);
        makeTag.setEffectStrength(10, 4,1);
        makeTag.setCastingDistance(30, 10, 5);
        makeTag.setLifetime(600, 300, 50);
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
        return ElementRegistry.FROST.get();
    }
}
