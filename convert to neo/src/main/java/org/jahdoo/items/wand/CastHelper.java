package org.jahdoo.items.wand;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.tags.TagHelper;

import static org.jahdoo.all_magic.AbstractAbility.DISTANCE_CAST;
import static org.jahdoo.utils.GeneralHelpers.chargeManaCost;
import static org.jahdoo.utils.tags.TagModifierHelper.MANA_COST;


public class CastHelper {

//    private static final List<Integer> getFloorCast = List.of(7, 9, 10, 15);


    private static void onSuccessfulCast(Player player, ItemStack itemStack){

        var abilityName = TagHelper.getAbilityTypeItemStack(itemStack);
//        var getCooldown = TagHelper.getWandAbilities(abilityName, player).getList(COOLDOWN, Tag.TAG_DOUBLE);
//        var getManaCost = TagHelper.getWandAbilities(abilityName, player).getList(MANA_COST, Tag.TAG_DOUBLE);

        AbstractAbility ability = AbilityRegister.REGISTRY.get(GeneralHelpers.modResourceLocation(abilityName));
        AbstractElement element = ElementRegistry.getElementByWandType(itemStack.getItem()).get(0);

        if(ability != null ){

            if(!ability.internallyChargeManaAndCooldown()){
//                setCastCost(abilityName, (int) getCooldown.getDouble(0), (int) getManaCost.getDouble(0), player, element);
            }

            ability.invokeAbility(player);
            player.stopUsingItem();
        }

    }

    public static void onCast(Player player){
        if (!TagHelper.getAbilityTypeWand(player).getPath().intern().isEmpty() && player.getTicksUsingItem() % 2 == 0) {
            if (GeneralHelpers.Random.nextInt(5) == 0) player.heal(0.5f);
            GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.SOUL_ESCAPE.value());
        }
    }

    public static InteractionResultHolder<ItemStack> use(ServerPlayer player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getMainHandItem();
        ResourceLocation abilityName = TagHelper.getAbilityTypeWand(player);
        AbstractAbility getAbility = AbilityRegister.REGISTRY.get(abilityName);

        if(player.isShiftKeyDown() || getAbility == null || player.level().isClientSide) {
            return InteractionResultHolder.fail(itemStack);
        }

        var getManaCost = TagHelper.getSpecificValue(player, itemStack, MANA_COST);
//        var cooldownSystem = CapabilityHelpers.getCooldownSystem(player);

        if(chargeManaCost(getManaCost, player)) {
            player.displayClientMessage(Component.literal(""), true);
//            if (!cooldownSystem.isAbilityOnCooldown(abilityName.getPath().intern())) {
//                if (getCanApplyDistanceAbility(player, itemStack)) {
//                    onCast(player);
//                    onSuccessfulCast(player, itemStack);
//                }
//            }
        } else {
            player.stopUsingItem();
        }

        return InteractionResultHolder.pass(itemStack);
    }

    public static boolean getCanApplyDistanceAbility(Player player, ItemStack itemStack){
//        CompoundTag compoundTag = TagHelper.getWandAbilitiesItemStack(TagHelper.getAbilityTypeItemStack(itemStack), itemStack);
        AtomicDouble number = new AtomicDouble();

        //Need to add messages here if can apply distance cast but cant reach location

//        compoundTag.getAllKeys().forEach(
//            keys -> {
//                if(keys.contains("Distance")) {
//                    ListTag listTag = compoundTag.getList(keys, Tag.TAG_DOUBLE);
//                    number.set(listTag.getDouble(0));
//                }
//            }
//        );

        HitResult lookAtLocation = player.pick(number.get() == 0 ? 20 : number.get(), 0, false);
        boolean isDistanceCast = AbilityRegister.REGISTRY.get(TagHelper.getAbilityTypeWand(player)).getCastType() == DISTANCE_CAST;
        boolean isValidCastLocation = lookAtLocation.getType() == HitResult.Type.MISS;
        if(!isDistanceCast || !isValidCastLocation) return true;

        player.stopUsingItem();
        return false;
    }

}
