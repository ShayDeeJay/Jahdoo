package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.all_abilities.abilities.ArcaneShiftAbility;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;

import static org.jahdoo.all_magic.all_abilities.abilities.ElementalShooterAbility.abilityId;
import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;

public class ArcaneShift {

    Player player;
    WandAbilityHolder wandAbilityHolder;

    public ArcaneShift(Player player) {
        this.player = player;
        this.wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
    }

    private TagModifierHelper getTag(){
        return new TagModifierHelper(wandAbilityHolder, ArcaneShiftAbility.abilityId.getPath().intern());
    }

    public void teleportToHome(){
        double damages = getTag().getModifierValue(DAMAGE);
        double distances = getTag().getModifierValue(ArcaneShiftAbility.distance);
        double maxEntity = getTag().getModifierValue(ArcaneShiftAbility.maxEntities);
        double lifeTimes = getTag().getModifierValue(ArcaneShiftAbility.lifeTime);
        Vec3 position = player.pick(distances, 0, false).getLocation();

//        TagModifierHelper setAbilities = new TagModifierHelper(wandAbilityHolder);
//        setAbilities.setDamageWithValue(8,3, (int) damages);
//        setAbilities.setIntValue(SET_ELEMENT_TYPE, ElementRegistry.MYSTIC.get().getTypeId());
//        this.compoundTag = setAbilities.getTagWithModifiers(abilityId.getPath().intern());;

        if(!player.level().isClientSide) {
            player.teleportTo(position.x, position.y + 1, position.z);
            shootSpikesRandomly(player, (int) maxEntity, 0.8, (int) lifeTimes);

            GeneralHelpers.getSoundWithPosition(player.level(), BlockPos.containing(position), SoundEvents.ENDERMAN_TELEPORT, 0.5f, 0.8f);
            GeneralHelpers.getSoundWithPosition(player.level(), BlockPos.containing(position), SoundRegister.ORB_FIRE.get(), 0.5f, 2f);
            player.resetFallDistance();
        }
    }



    private void shootSpikesRandomly(Player player, int maxEntities, double velocity, int discardTime){
        double centerY = player.getY() + player.getBbHeight() / 2;
        double angleIncrement = 2 * Math.PI / maxEntities;

//        if(compoundTag != null){
//            for (int i = 0; i < maxEntities; i++) {
//                float speeds = GeneralHelpers.Random.nextFloat((float) velocity - 0.3f, (float) velocity);
//                double theta = i * angleIncrement;
//
//                double x = Math.cos(theta);
//                double z = Math.sin(theta);
//
//                GenericProjectile genericProjectile = new GenericProjectile(
//                    player, player.getX(), centerY - 0.5, player.getZ(),
//                    ProjectilePropertyRegister.ELEMENTAL_SHOOTER.get().setAbilityId(), compoundTag
//                );
//                genericProjectile.setCustomDiscardTime(discardTime);
//                genericProjectile.setOwner(player);
//                genericProjectile.shoot(x, 0, z, speeds, 0); // Set the y component of direction to 0
//                player.level().addFreshEntity(genericProjectile);
//            }
//        }
    }
}
