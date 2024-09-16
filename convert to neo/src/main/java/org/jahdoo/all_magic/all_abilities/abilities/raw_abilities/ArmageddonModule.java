package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.FireballAbility;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.tags.TagHelper;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;

public class ArmageddonModule extends DefaultEntityBehaviour {

    public static final String name = "armageddon_module";
    int privateTicks;
    double aoe = 0.05;

    public WandAbilityHolder armageddonFireballModifiers() {
        TagModifierHelper makeTags = new TagModifierHelper();
        double damageA = TagHelper.getSpecificValue(name, this.aoeCloud.getwandabilityholder(), DAMAGE);
        makeTags.setDamageWithValue(0,0, damageA);
        makeTags.setEffectDurationWithValue(0,0,200);
        makeTags.setEffectChanceWithValue(0,0,20);
        makeTags.setEffectStrengthWithValue(0,0,0);
        makeTags.setAbilityTagModifiers(FireballAbility.novaRange, 0,0,true, GeneralHelpers.Random.nextInt(4,6));
        makeTags.setModifiers(FireballAbility.abilityId.getPath().intern());
        return makeTags.getHolder();
    }

    @Override
    public TagModifierHelper getTag() {
        return new TagModifierHelper(this.aoeCloud.getwandabilityholder(), name);
    }

    @Override
    public void onTickMethod() {
        fireballNova(aoeCloud.getRandomRadius());
        if(aoe >= aoeCloud.getRandomRadius()) {
            privateTicks++;
            if(privateTicks == 10) setProjectile();
        }

        if(privateTicks < 20) return;

        aoe -= 0.4;
        if(aoe < 0) {
            if(!(aoeCloud.level() instanceof  ServerLevel serverLevel)) return;
            ParticleHandlers.spawnPoof(
                serverLevel, aoeCloud.position(), 5,
                ElementRegistry.MYSTIC.get().getParticleGroup().genericSlow(),0,0,0,0.1f
            );
            aoeCloud.discard();
        }
    }

    @Override
    public void discardCondition() {
        if(aoeCloud.tickCount > 100) aoeCloud.discard();
    }

    private void setProjectile(){
        if (!(aoeCloud.level() instanceof ServerLevel serverLevel)) return;
        if(this.aoeCloud.getOwner() != null){
            float setRandomYHeight = GeneralHelpers.Random.nextFloat(0.3f, 0.6f);

            ElementProjectile fireProjectile = new ElementProjectile(
                EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(),
                this.aoeCloud.getOwner(), aoeCloud.getX(), aoeCloud.getY(), aoeCloud.getZ(),
                ProjectilePropertyRegister.FIRE_BALL.get().setAbilityId(),
                armageddonFireballModifiers()
            );

            fireProjectile.setIsChildObject(true);

            fireProjectile.shoot(0, aoeCloud.getY(), 0, aoeCloud.getY() > 0 ? -setRandomYHeight : setRandomYHeight, 0);
            fireProjectile.setOwner(this.aoeCloud.getOwner());
            this.aoeCloud.getOwner().level().addFreshEntity(fireProjectile);
            GeneralHelpers.getSoundWithPosition(aoeCloud.level(), aoeCloud.blockPosition(), SoundRegister.ORB_CREATE.get(), 0.3f, 1.0f);

            GeneralHelpers.getInnerRingOfRadiusRandom(aoeCloud.position(), aoe - 1, 20,
                positions -> {
                    GeneralHelpers.generalHelpers.sendParticles(
                        serverLevel,
                        ElementRegistry.MYSTIC.get().getParticleGroup().magicSlow(),
                        positions, 2, 0.1, 0.5, 0.1, 0.05
                    );
                }
            );
        }
    }

    void fireballNova(double novaMaxSize){
        if(!(aoeCloud.level() instanceof ServerLevel serverLevel)) return;
        if(aoe <= novaMaxSize) aoe += 0.2;
        double particle1 = 0.08 ;
        double particle2 = 0.01;

        GeneralHelpers.getOuterRingOfRadius(aoeCloud.position(), aoe, Math.max(aoe * 16, 10),
            positions -> {
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    new BakedParticleOptions(ElementRegistry.MYSTIC.get().getTypeId(), 4,4, false),
                    positions, 1, 0.05, 0.05, 0.05, particle1
                );
                GeneralHelpers.generalHelpers.sendParticles(
                    serverLevel,
                    genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, ElementRegistry.MYSTIC.get(), 8, 5f),
                    positions, 1, 0.05, 0.05, 0.05, particle2
                );
            }
        );
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("private_ticks", privateTicks);
        compoundTag.putDouble("aoe", aoe);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.aoe = compoundTag.getDouble("aoe");
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("armageddon_module_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ArmageddonModule();
    }
}
