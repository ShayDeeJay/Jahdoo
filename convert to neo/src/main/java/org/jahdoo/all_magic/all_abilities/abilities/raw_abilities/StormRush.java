package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.all_abilities.abilities.StormRushAbility;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.CustomMobEffect;
import org.jahdoo.utils.tags.TagModifierHelper;

public class StormRush {

    Player player;
    WandAbilityHolder wandAbilityHolder;

    public StormRush(Player player){
        this.player = player;
        this.wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
    }

    public void launchPlayerDirection() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if(localPlayer != null){
//            TagModifierHelper getModifierWithTag = new TagModifierHelper(compoundTag, StormRushAbility.abilityId.getPath().intern());
            TagModifierHelper getModifierWithTag = new TagModifierHelper(wandAbilityHolder, StormRushAbility.abilityId.getPath().intern());

            double launchDistances = getModifierWithTag.getModifierValue(StormRushAbility.launchDistance);

            player.level().playSound(null, player.blockPosition(), SoundRegister.DASH_EFFECT.get(), SoundSource.NEUTRAL,0.5f,2);
            player.level().playSound(null, player.blockPosition(), SoundRegister.BOLT.get(), SoundSource.NEUTRAL,0.5f,0.8f);
            player.addEffect(new CustomMobEffect(EffectsRegister.NO_FALL_DAMAGE.getDelegate(), 200, 1));

            Vec3 lookVector = localPlayer.getLookAngle().scale(launchDistances);
            localPlayer.setDeltaMovement(lookVector);
        }

//        if(player.level() instanceof ServerLevel serverLevel){
//            spawnElectrifiedParticles(serverLevel, localPlayer.position(), this.getType().getParticleGroup().bakedSlow(), 30,player,0.08);
//            spawnElectrifiedParticles(serverLevel, localPlayer.position(), this.getType().getParticleGroup().magic(), 30, player, 0.08);
//        }
    }
    AbstractElement getType(){
        return ElementRegistry.LIGHTNING.get();
    }


}
