package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.ArmageddonAbility;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jahdoo.utils.tags.TagModifierHelper.DAMAGE;
import static org.jahdoo.utils.tags.TagModifierHelper.LIFETIME;

public class Armageddon extends DefaultEntityBehaviour {

    double aoe;
    double spawnSpeed;
    double damage;
    double lifetime;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        this.aoe = this.getTag().getModifierValue(ArmageddonAbility.aoe);
        this.spawnSpeed = this.getTag().getModifierValue(ArmageddonAbility.speed);
        this.damage = DamageCalculator.getCalculatedDamage(this.aoeCloud.getOwner(), this.getTag().getModifierValue(DAMAGE));
        this.lifetime = this.getTag().getModifierValue(LIFETIME);
    }

    @Override
    public TagModifierHelper getTag() {
        return new TagModifierHelper(this.aoeCloud.getwandabilityholder(), ArmageddonAbility.abilityId.getPath().intern());
    }

    @Override
    public void onTickMethod() {
        aoeCloud.setRadius((float) aoe / 2);

        if(aoeCloud.tickCount % spawnSpeed == 0 || aoeCloud.tickCount == 0){
            List<Vec3> getPositionInRadius = GeneralHelpers.getInnerRingOfRadiusRandom(aoeCloud.position(), aoe + 2, 100);
            this.createModule(getPositionInRadius.get(GeneralHelpers.Random.nextInt(0, getPositionInRadius.size()-1)));
        }
    }

    @Override
    public void discardCondition() {
        if(aoeCloud.tickCount > lifetime) aoeCloud.discard();
    }

    public void setExternalAbilityModifiers(CompoundTag compoundTag, String name, double value){
        compoundTag.put(name, GeneralHelpers.nbtDoubleList(value, 0, 0, 0));
    }

    public AbilityHolder setAbilityModifiers(String name, double value){
        AbilityHolder.AbilityModifiers abilityModifiers = new AbilityHolder.AbilityModifiers(value, 0,0,true);
        return new AbilityHolder(Map.of(name, abilityModifiers));
    }

//    public WandAbilityHolder armageddonModule() {
//        DataComponentType.builder().build();
//
//        return TagHelper.getWandAbilities();
//    }

    public WandAbilityHolder armageddonModule() {
        WandAbilityHolder wandAbilityHolder = new WandAbilityHolder(new HashMap<>());
        wandAbilityHolder.abilityProperties().put(ArmageddonModule.name, this.setAbilityModifiers(DAMAGE, this.damage));
        return wandAbilityHolder;
    }

    private void createModule(Vec3 location){
        AoeCloud aoeCloud = new AoeCloud(
            this.aoeCloud.level(),
            this.aoeCloud.getOwner(), 0.2f,
            ProjectilePropertyRegister.ARMAGEDDON_MODULE.get().setAbilityId(),
            armageddonModule()
        );
        aoeCloud.setPos(location.x, location.y + GeneralHelpers.Random.nextInt(6, 12), location.z);
        aoeCloud.level().addFreshEntity(aoeCloud);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(ArmageddonAbility.aoe, this.aoe);
        compoundTag.putDouble(ArmageddonAbility.speed, this.spawnSpeed);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(LIFETIME, this.lifetime);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.aoe = compoundTag.getDouble(ArmageddonAbility.aoe);
        this.spawnSpeed = compoundTag.getDouble(ArmageddonAbility.speed);
        this.damage = compoundTag.getDouble(DAMAGE);
        this.lifetime = compoundTag.getDouble(LIFETIME);
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("armageddon_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Armageddon();
    }
}
