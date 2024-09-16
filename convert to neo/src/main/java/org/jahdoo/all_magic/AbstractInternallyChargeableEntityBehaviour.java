package org.jahdoo.all_magic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static org.jahdoo.utils.GeneralHelpers.setCastCost;

public abstract class AbstractInternallyChargeableEntityBehaviour extends DefaultEntityBehaviour {

    public boolean chargeInternalCastingCost(ResourceLocation location, double cooldown, double mana, Player player){
        return setCastCost(location.getPath().intern(), (int) cooldown, (int) mana, player, this.getElementType());
    }

}
