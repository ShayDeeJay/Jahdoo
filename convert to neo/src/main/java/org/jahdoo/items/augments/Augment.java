package org.jahdoo.items.augments;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Augment extends Item {

    public Augment(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getDefaultMaxStackSize() {
        return 1;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if (!itemStack.getComponents().has(DataComponentRegistry.WAND_ABILITY_HOLDER.get())) {
            if(level instanceof ServerLevel serverLevel) AugmentItemHelper.setDiscoveryTheme(serverLevel, player);
            AugmentItemHelper.handleStackedAugments(itemStack, player, interactionHand);
            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
    }

    @Override
    public @NotNull Component getName(ItemStack itemStack) {
        CustomModelData modelData = itemStack.getComponents().get(DataComponents.CUSTOM_MODEL_DATA);
        if(modelData != null){
            List<AbstractElement> abstractElement = ElementRegistry.REGISTRY
                .stream()
                .filter(ability -> ability.getTypeId() == modelData.value())
                .toList();

            if (!abstractElement.isEmpty()) {
                if (itemStack.getComponents().has(DataComponentRegistry.WAND_ABILITY_HOLDER.get())) {
                    return this.getAbilityName(itemStack, abstractElement.get(0));
                }
                String elementName = abstractElement.get(0).getElementName() + " Augment";
                int elementColour = abstractElement.get(0).textColourPrimary();
                return Component.literal(elementName).withStyle(style -> style.withColor(elementColour));
            }
        }
        return Component.literal("Unidentified Augment").withStyle(style -> style.withColor(-9013642));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        AugmentItemHelper.getHoverText(pStack, pTooltipComponents);
    }



    private Component getAbilityName(ItemStack itemStack, AbstractElement info){
        WandAbilityHolder wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        AtomicReference<Component> component = new AtomicReference<>(Component.empty());

        if(wandAbilityHolder != null){
            wandAbilityHolder.abilityProperties().keySet().stream().findFirst().ifPresent(
                s -> {
                    AbstractAbility abstractAbility = AbilityRegister.REGISTRY.get(GeneralHelpers.modResourceLocation(s));
                    component.set(Component.literal(abstractAbility.getAbilityName()).withStyle((style) -> style.withColor(info.textColourPrimary())));
                }
            );
        }

        return component.get();
    }
}
