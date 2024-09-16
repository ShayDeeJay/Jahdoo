package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.Decoy;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class EscapeDecoy extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("escape_decoy");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(20, 10,  1);
        makeTag.setCooldown(400, 100, 50);
        makeTag.setLifetime(300, 100, 20);
        makeTag.setModifiers(itemStack, abilityId.getPath().intern());
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
    }

    @Override
    public int getCastType() {
        return AREA_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.VITALITY.get();
    }

    @Override
    public void invokeAbility(Player player) {
        Decoy decoy = new Decoy(player.level(), player);
//        TagModifierHelper tagModifier = new TagModifierHelper(
//            player.getMainHandItem()
//                .getOrCreateTag()
//                .getCompound(WAND_STORED_ABILITIES)
//                .getCompound(abilityId.getPath().intern())
//        );


//        decoy.setMaxLifetime((int) tagModifier.getPropertyFromCompound(LIFETIME));
        decoy.setMaxLifetime(100);

        Vec3 lookVector = player.getLookAngle();
//        player.addEffect(new CustomMobEffect(MobEffects.MOVEMENT_SPEED, 50, 6));
//        player.addEffect(new CustomMobEffect(EffectsRegister.STEP_BOOST.get(), 50, 1));
        double yaw = Math.toDegrees(Math.atan2(lookVector.z, lookVector.x)) + 90.0;
        decoy.setYRot((float) yaw);
        decoy.setYHeadRot((float) yaw);
        decoy.setYBodyRot((float) yaw);
        decoy.setPos(player.getX(), player.getY(), player.getY());
        decoy.yRotO = (float) yaw;
        decoy.yHeadRotO = (float) yaw;
        decoy.setNoAi(true);
        decoy.moveTo(player.position());
        player.level().addFreshEntity(decoy);
    }
}
