package org.jahdoo.items.wand;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.abilityAttributes.AbstractAttribute;
import org.jahdoo.utils.abilityAttributes.CooldownCalculator;
import org.jahdoo.utils.abilityAttributes.DamageCalculator;
import org.jahdoo.utils.abilityAttributes.ManaCalculator;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.particle.ParticleStore.rgbToInt;


public class WandItemHelper {

    public static Component defence = Component.literal("\uD83D\uDEE1Defence").withStyle(color -> color.withColor(rgbToInt(102, 178, 255)));
    public static Component attack = Component.literal("\uD83D\uDDE1Attack").withStyle(color -> color.withColor(rgbToInt(255, 102, 102)));
    public static Component getItemName(ItemStack wandType){
        AbstractElement abstractElement = ElementRegistry.getElementByWandType(wandType.getItem()).get(0);
        if(abstractElement != null){
            return Component.literal("Wand of " + abstractElement.getElementName())
                .withStyle(style -> style.withColor(abstractElement.textColourPrimary()));
        }
        return Component.empty();
    }

    public static void setWizardMode(LivingEntity pInteractionTarget, Player pPlayer){
        String p1 = "Eternal Wizard in ";
        String p2 = " mode";

        if(pInteractionTarget instanceof EternalWizard eternalWizard){
            if (eternalWizard.modeType()) {
                eternalWizard.setMode(false);
                pPlayer.displayClientMessage(Component.literal(p1).append(defence).append(p2), true);
            } else {
                eternalWizard.setMode(true);
                pPlayer.displayClientMessage(Component.literal(p1).append(attack).append(p2), true);
            }
        }
    }

    public static List<Component> getItemModifiers(ItemStack wandItem){
        List<Component> appendComponents = new ArrayList<>();
        int colour = rgbToInt(198, 198, 198);
        int colour2 = rgbToInt(145, 145, 145);

        float getDamageMultiplier = AbstractAttribute.getAttributeValue(wandItem, DamageCalculator.DAMAGE_MULTIPLIER);
        float getCooldownReduction = AbstractAttribute.getAttributeValue(wandItem, CooldownCalculator.COOLDOWN_REDUCTION);
        float getManaReduction = AbstractAttribute.getAttributeValue(wandItem, ManaCalculator.MANA_REDUCTION);

        appendComponents.add(Component.empty());
        Component damage = withStyleComponent("+"+getDamageMultiplier+"% damage multiplier", colour2);
        Component cooldownReduction = withStyleComponent("+"+getCooldownReduction+"% cooldown reduction", colour2);
        Component manaCostReduction = withStyleComponent("+"+getManaReduction+"% mana cost reduction", colour2);
        Component castPerkDescription = withStyleComponent("Apply to all ", colour);
        Component castPerkDescription2 = withStyleComponent(" abilities", colour);

        AbstractElement abstractElement = ElementRegistry.getElementByWandType(wandItem.getItem()).get(0);

        Component type = withStyleComponent(abstractElement.getElementName(), abstractElement.particleColourSecondary());
        appendComponents.add(castPerkDescription.copy().append(type).append(castPerkDescription2));
        appendComponents.add(damage.copy());
        appendComponents.add(cooldownReduction.copy());
        appendComponents.add(manaCostReduction.copy());

        appendComponents.add(Component.empty());

        WandSlotManager.getSlot(wandItem, appendComponents);

        return appendComponents;
    }



    public static Component withStyleComponent(String text, int colour){
        return Component.literal(text).withStyle(style -> style.withColor(colour));
    }
}
