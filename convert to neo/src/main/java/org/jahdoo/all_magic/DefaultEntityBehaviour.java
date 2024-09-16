package org.jahdoo.all_magic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.utils.tags.TagModifierHelper;

public abstract class DefaultEntityBehaviour extends AbstractEntityProperty {
    protected AoeCloud aoeCloud;
    protected ElementProjectile elementProjectile;
    protected GenericProjectile genericProjectile;
    public TagModifierHelper getTag(){ return null; }

    public AbstractElement getElementType(){return null;}

    public void getElementProjectile(ElementProjectile elementProjectile) { this.elementProjectile = elementProjectile; }
    public void getGenericProjectile(GenericProjectile genericProjectile) { this.genericProjectile = genericProjectile; }
    public void getAoeCloud(AoeCloud aoeCloud){ this.aoeCloud = aoeCloud; }

    public void onBlockBlockHit(BlockHitResult blockHitResult){}

    public void onEntityHit(LivingEntity hitEntity){}

    public void onTickMethod(){}

    public void discardCondition(){}

    public void addAdditionalDetails(CompoundTag compoundTag){}

    public void readCompoundTag(CompoundTag compoundTag){}

    public boolean damageEntity(LivingEntity hitEntity){
        return !(hitEntity instanceof EternalWizard) && !(hitEntity instanceof Player);
    }

}
