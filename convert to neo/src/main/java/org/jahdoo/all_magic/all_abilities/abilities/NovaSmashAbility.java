package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.tags.TagModifierHelper;

public class NovaSmashAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("nova_smash");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    public static BlockHitResult getEntityBlockInteraction(LivingEntity livingEntity ){
        AABB livingEntityBoundingBox = livingEntity.getBoundingBox();
        Vec3 startVec = new Vec3(livingEntityBoundingBox.minX - 0.1, livingEntityBoundingBox.minY, livingEntityBoundingBox.minZ - 0.1);
        Vec3 endVec = new Vec3(livingEntityBoundingBox.maxX, livingEntityBoundingBox.maxY, livingEntityBoundingBox.maxZ);

        return livingEntity.level().clip(
            new ClipContext(
                startVec,
                endVec,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE, livingEntity
            )
        );
    }

    @Override
    public void invokeAbility(Player player) {
//        if(!player.onGround()) {
//            player.getCapability(org.jahdoo.capabilities.player_abilities.NovaSmashAbility.GET_NOVA_SMASH).ifPresent(
//                novaSmashAbility -> novaSmashAbility.setCanSmash(true)
//            );
//        }
    }

    @Override
    public void setModifiers(ItemStack itemStack, Player player) {
        TagModifierHelper makeTag = new TagModifierHelper(player, itemStack);
        makeTag.setMana(20, 5,  1);
        makeTag.setCooldown(60, 20, 5);
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
        return ElementRegistry.MYSTIC.get();
    }
}
