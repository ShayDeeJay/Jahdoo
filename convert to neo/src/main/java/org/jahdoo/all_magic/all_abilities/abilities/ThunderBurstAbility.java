package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;

import static org.jahdoo.all_magic.all_abilities.ability_components.LightningTrail.getLightningTrailModifiers;
import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;

public class ThunderBurstAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("thunder_burst");
    public static final String NUMBER_OF_THUNDERBOLTS = "Number of Thunderbolts";

    private TagModifierHelper tagModifierHelper(Player player){
        WandAbilityHolder wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
//        CompoundTag compoundTag = player.getMainHandItem().getOrCreateTag();
//        return new TagModifierHelper(compoundTag, abilityId.getPath().intern());
        return new TagModifierHelper(wandAbilityHolder, abilityId.getPath().intern());
    }

    @Override
    public void invokeAbility(Player player) {
        TagModifierHelper compoundTag = this.tagModifierHelper(player);
        double damage = DamageCalculator.getCalculatedDamage(player, compoundTag.getModifierValue(DAMAGE));
        double numberOfBolts = compoundTag.getModifierValue(NUMBER_OF_THUNDERBOLTS);

        Vec3 direction = player.getLookAngle();
        CompoundTag lightningTrailModifiers = getLightningTrailModifiers(damage, 0.2, 10, true);
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.BOLT.get(), 2f,1f);

        for(int i = 0; i < numberOfBolts; i++){
//            GenericProjectile genericProjectile = new GenericProjectile(player, 0, ProjectilePropertyRegister.LIGHTNING_TRAIL.get().setAbilityId(), lightningTrailModifiers, -2);
//            genericProjectile.shoot(direction.x(), direction.y(), direction.z(), 1f, 0);
//            genericProjectile.setOwner(player);
//            player.level().addFreshEntity(genericProjectile);
        }
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(50, 20,  1);
        makeTag.setCooldown(600, 200, 100);
        makeTag.setDamage(25, 10, 1);
        makeTag.setAbilityTagModifiersRandom(NUMBER_OF_THUNDERBOLTS, 30,5, true, 5);
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
