package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.utils.tags.TagHelper;
import org.jahdoo.utils.tags.TagModifierHelper;

import java.util.Collections;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;

public class BoltzAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("boltz");
    public static final String dischargeRadius = "Discharge Radius";
    public static final String totalBolts = "Total Boltz";

    @Override
    public void invokeAbility(Player player) {
//        if(compoundTag == null) return;
//        int totalShots = (int) new TagModifierHelper(compoundTag, abilityId.getPath().intern()).getModifierValue(totalBolts);
        int totalShots = (int) TagHelper.getSpecificValue(player, player.getMainHandItem(), totalBolts);

        Vec3 direction = player.getLookAngle();

        GenericParticleOptions particleOptions = genericParticleOptions(ParticleStore.ELECTRIC_PARTICLE_SELECTION, this.getElemenType(), 5, 1.2f, 0.5);

        genericParticleOptions(this.getElemenType(), 30, 1f);

        for (int i = 0; i < totalShots; i++) {
            ElementProjectile elementProjectile = new ElementProjectile(EntitiesRegister.LIGHTNING_ELEMENT_PROJECTILE.get(), player, ProjectilePropertyRegister.BOLTZ.get().setAbilityId(), 0);
            double spread = 0.9; // Adjust the spread value as needed
            double spreadX = direction.x + (Math.random() - 0.5) * spread;
            double spreadY = direction.y + (Math.random() - 0.5) * spread;
            double spreadZ = direction.z + (Math.random() - 0.5) * spread;
            elementProjectile.shoot(spreadX, spreadY, spreadZ, 0.5f, 0);
            elementProjectile.setOwner(player);
            player.level().addFreshEntity(elementProjectile);
        }

        for(int i = 0; i < totalShots * 20; i++){
            double spread = 0.8; // Adjust the spread value as needed
            double spreadX = direction.x + (Math.random() - 0.5) * spread;
            double spreadY = direction.y + (Math.random() - 0.5) * spread;
            double spreadZ = direction.z + (Math.random() - 0.5) * spread;
            if(player.level() instanceof ServerLevel serverLevel){
                GeneralHelpers.generalHelpers.sendParticles(serverLevel, particleOptions, player.position().add(0,1.5,0), 0, spreadX, spreadY, spreadZ, 1);
            }
        }

        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.ORB_CREATE.get(), 0.5f,1.5f);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(20, 10,  1);
        makeTag.setCooldown(400, 100, 50);
        makeTag.setDamage(15, 5, 1);
        makeTag.setEffectDuration(300, 100, 50);
        makeTag.setEffectStrength(10, 0,1);
        makeTag.setEffectChance(20,5,5);
        makeTag.setAbilityTagModifiersRandom(dischargeRadius, 3,1, true, 1);
        makeTag.setAbilityTagModifiersRandom(totalBolts, 6,2, true, 1);
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
