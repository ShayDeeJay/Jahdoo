package org.jahdoo.capabilities.player_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.all_abilities.abilities.StaticAbility;
import org.jahdoo.capabilities.AbstractCapability;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.tags.TagHelper;

import java.util.Map;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.utils.tags.TagModifierHelper.*;

public class Static implements AbstractCapability {

    private boolean isActive;
    double damageA;
    double manaPerHitA;
    double rangeA;
    double effectDurationA;
    double effectStrengthA;
    double effectChanceA;
    double manaCost;
    double cooldownCost;

    @Override
    public void saveNBTData(CompoundTag nbt) {
        nbt.putBoolean("lastJumped", this.isActive);
        nbt.putDouble(DAMAGE, this.damageA);
        nbt.putDouble(StaticAbility.mana_per_damage, this.manaPerHitA);
        nbt.putDouble(RANGE, this.rangeA);
        nbt.putDouble(EFFECT_DURATION, this.effectDurationA);
        nbt.putDouble(EFFECT_STRENGTH, this.effectStrengthA);
        nbt.putDouble(EFFECT_CHANCE, this.effectChanceA);
        nbt.putDouble(MANA_COST, this.manaCost);
        nbt.putDouble(COOLDOWN, this.cooldownCost);

    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        this.isActive = nbt.getBoolean("lastJumped");
        this.damageA = nbt.getDouble(DAMAGE);
        this.manaPerHitA = nbt.getDouble(StaticAbility.mana_per_damage);
        this.rangeA = nbt.getDouble(RANGE);
        this.effectDurationA = nbt.getDouble(EFFECT_DURATION);
        this.effectStrengthA = nbt.getDouble(EFFECT_STRENGTH);
        this.effectChanceA = nbt.getDouble(EFFECT_CHANCE);
        this.manaCost = nbt.getDouble(MANA_COST);
        this.cooldownCost = nbt.getDouble(COOLDOWN);
    }

    @Override
    public void copyFrom(AbstractCapability source) {
        if(source instanceof Static staticAbility){
            this.isActive = staticAbility.isActive;
        }
    }

    public void activate(Player player){
        Map<String, AbilityHolder.AbilityModifiers> wandAbilityHolder = TagHelper.getSpecificValue(player);
        this.damageA = wandAbilityHolder.get(DAMAGE).actualValue();
        this.manaPerHitA = wandAbilityHolder.get(StaticAbility.mana_per_damage).actualValue();
        this.rangeA = wandAbilityHolder.get(RANGE).actualValue();
        this.effectDurationA = wandAbilityHolder.get(EFFECT_DURATION).actualValue();
        this.effectStrengthA = wandAbilityHolder.get(EFFECT_STRENGTH).actualValue();
        this.effectChanceA = wandAbilityHolder.get(EFFECT_CHANCE).actualValue();
        this.manaCost = wandAbilityHolder.get(MANA_COST).actualValue();
        this.cooldownCost = wandAbilityHolder.get(COOLDOWN).actualValue();
        GeneralHelpers.chargeManaCost((int) this.manaCost, player);
        this.isActive = true;
    }

    public void deactivate(Player player){
        GeneralHelpers.setAbilityCooldown(StaticAbility.abilityId.getPath().intern(), (int) this.cooldownCost, player, this.getType());
        this.isActive = false;
    }

    public boolean getIsActive(){
        return this.isActive;
    }

    public void onTickMethod(Player player) {
        if(player == null) return;

        if(this.getIsActive()){
            if(player.getMainHandItem().getItem() instanceof WandItem){
//                if (!TagHelper.getWandAbilities(StaticAbility.abilityId.getPath().intern(), player).isEmpty()) {
//                    if (GeneralHelpers.chargeManaCost(manaPerHitA, player)) {
//                        if (!(player.level() instanceof ServerLevel serverLevel)) return;
//                        int getRandomChance = GeneralHelpers.Random.nextInt(0, effectChanceA == 0 ? 20 : Math.max((int) effectChanceA, 10));
//                        if(getRandomChance == 0) setEffectParticle(player, serverLevel);
//
//                        this.damageAttackingEntity(player, serverLevel, getRandomChance);
//                    } else {
//                        this.deactivate(player);
//                    }
//                }
            }
        }
    }

    public static void setEffectParticle(
        LivingEntity targetEntity,
        ServerLevel serverLevel
    ){
        if(targetEntity.isAlive()){
            GeneralHelpers.getSoundWithPosition(targetEntity.level(), targetEntity.blockPosition(), SoundRegister.BOLT.get(), 0.05f, 1.5f);

            GenericParticleOptions particleOptions = genericParticleOptions(
                ParticleStore.ELECTRIC_PARTICLE_SELECTION,
                ElementRegistry.LIGHTNING.get(),
                GeneralHelpers.Random.nextInt(5, 8),
                1.5f,
                0.8
            );

            int particleCount = targetEntity instanceof Player ? 5 : 30;
            spawnElectrifiedParticles(serverLevel, targetEntity.position(), particleOptions, particleCount, targetEntity, 0);
        }
    }

    private void damageAttackingEntity(Player player, ServerLevel serverLevel, int getRandomChance){
//        ManaSystem manaSystem = CapabilityHelpers.getManaSystem(player);
        player.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, player,
            player.getBoundingBox().inflate(rangeA)
        ).forEach(
            entities -> {
                if (entities == player || entities instanceof EternalWizard) return;
                if (player.getLastHurtByMob() == entities && player.hurtTime > 8) {

                    GeneralHelpers.getSoundWithPosition(
                        entities.level(),
                        entities.blockPosition(),
                        SoundRegister.BOLT.get(),
                        1f
                    );

                    setEffectParticle(entities, serverLevel);
//                    manaSystem.subtractMana(manaPerHitA);

                    entities.hurt(
                        player.damageSources().magic(),
                        GeneralHelpers.getMagicDamageAmplifier(player, (int) damageA,  this.getType())
                    );

                    if (getRandomChance == 0) {
                        entities.addEffect(
                            new CustomMobEffect(
                                EffectsRegister.LIGHTNING_EFFECT.getDelegate(),
                                (int) effectDurationA,
                                (int) effectStrengthA
                            )
                        );
                    }

                    player.setLastHurtByMob(null);
                }
            }
        );
    }

    AbstractElement getType(){
        return ElementRegistry.LIGHTNING.get();
    }
}
