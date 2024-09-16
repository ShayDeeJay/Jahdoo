package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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

public class SummonEternalWizardAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("eternal_wizard");

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0f, ProjectilePropertyRegister.SUMMON_ETERNAL_WIZARD.get().setAbilityId());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
        player.level().playSound(null, BlockPos.containing(location), SoundEvents.WITHER_AMBIENT, SoundSource.BLOCKS, 2f, 0.8f);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.EXPLOSION.get(), SoundSource.BLOCKS, 2f, 1.2f);

    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(50, 20,  5);
        makeTag.setCooldown(1800, 1200, 100);
        makeTag.setDamage(15, 5, 1);
        makeTag.setEffectStrength(10, 0, 1);
        makeTag.setEffectDuration(600, 200, 50);
        makeTag.setEffectChance(50, 20, 5);
        makeTag.setCastingDistance(20, 10, 1);
        makeTag.setLifetime(1200, 200, 50);
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
        return ElementRegistry.VITALITY.get();
    }

}
