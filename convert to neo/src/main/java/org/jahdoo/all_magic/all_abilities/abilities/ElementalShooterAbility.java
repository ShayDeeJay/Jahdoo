package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

import static org.jahdoo.utils.tags.TagModifierHelper.SET_ELEMENT_TYPE;

public class ElementalShooterAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("elemental_shooter");
    public static final String numberOfProjectiles = "Shot Multiplier";
    public static final String numberOfRicochet = "Ricochets";
    public static final String velocity = "Projectile Velocity";

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper setAbilities = new TagModifierHelper(player, itemStack);
        setAbilities.setMana(20,10, 1);
        setAbilities.setCooldown(100, 0, 10);
        setAbilities.setDamage(10,5,1);
        setAbilities.setEffectChance(50, 10, 5);
        setAbilities.setEffectStrength(10,1,1);
        setAbilities.setEffectDuration(300,100,20);
        setAbilities.setAbilityTagModifiersRandom(numberOfProjectiles, 3, 1, true, 1);
        setAbilities.setAbilityTagModifiersRandom(numberOfRicochet, 6, 1,  true,1);
        setAbilities.setAbilityTagModifiersRandom(velocity, 1.5, 0.6,  true,0.1);
        setAbilities.setAbilityTagModifiers(SET_ELEMENT_TYPE, 0,0,false, GeneralHelpers.Random.nextInt(1,6));
        setAbilities.setModifiers(itemStack, abilityId.getPath().intern());
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
        return null;
    }

    private Vec3 calculateDirectionOffset(LivingEntity player, double offset) {
        Vec3 lookDirection = player.getLookAngle();
        Vec3 rightVector = new Vec3(-lookDirection.z(), 0, lookDirection.x()).normalize(); // Perpendicular to look direction
        return rightVector.scale(offset);
    }

    private TagModifierHelper getTag(Player player){
        return new TagModifierHelper(player.getMainHandItem(), abilityId.getPath().intern());
    }

    @Override
    public void invokeAbility(Player player) {
        double numberOfProjectile = getTag(player).getModifierValue(ElementalShooterAbility.numberOfProjectiles);
        double velocities = getTag(player).getModifierValue(ElementalShooterAbility.velocity);
        double totalWidth = (numberOfProjectile - 1) * 0.1; // Adjust the total width as needed
        double startOffset = -totalWidth / 2.0;


        for (int i = 0; i < numberOfProjectile; i++) {
            double offset = numberOfProjectile == 1 ? 0 : startOffset + i * (totalWidth / (numberOfProjectile - 1));
            GenericProjectile genericProjectile = new GenericProjectile(player,0, ProjectilePropertyRegister.ELEMENTAL_SHOOTER.get().setAbilityId());
            Vec3 directionOffset = calculateDirectionOffset(player, offset);
            Vec3 direction = player.getLookAngle().add(directionOffset).normalize();
            genericProjectile.shoot(direction.x(), direction.y(), direction.z(), (float) velocities, 0);
            genericProjectile.setOwner(player);
            player.level().addFreshEntity(genericProjectile);
        }
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.ENDER_EYE_DEATH, 0.25f);
    }

    @Override
    public boolean isMultiType() {
        return true;
    }
}
