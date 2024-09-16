package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;

public class FrostboltsAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("frostbolts");
    public static final String NUMBER_OF_PROJECTILES = "Total Arrows";
    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }


    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(20, 10,  1);
        makeTag.setCooldown(500, 100, 50);
        makeTag.setAbilityTagModifiersRandom(NUMBER_OF_PROJECTILES, 30,5, true, 5);
//        makeTag.setAbilityTagModifiers(DAMAGE, 30, 5, true, makeTag.getPropertyFromCompound(NUMBER_OF_PROJECTILES));
        makeTag.setEffectDuration(300, 100, 20);
        makeTag.setEffectStrength(10, 0,1);
        makeTag.setEffectChance(40,5,5);
        makeTag.setCastingDistance(30,5,5);
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

    @Override
    public boolean internallyChargeManaAndCooldown() {
        return true;
    }

    @Override
    public void invokeAbility(Player player) {
        if(player != null){
            GenericProjectile elementProjectile = new GenericProjectile(player, 0, ProjectilePropertyRegister.FROST_BOLT.get().setAbilityId());
            Vec3 direction = player.getLookAngle();
            elementProjectile.shoot(direction.x(), direction.y(), direction.z(), 20, 0);
            elementProjectile.setOwner(player);
            elementProjectile.setInvisible(true);
            player.level().addFreshEntity(elementProjectile);
        }
    }
}
