package org.jahdoo.all_magic;

import net.minecraft.nbt.CompoundTag;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.utils.tags.TagModifierHelper;

public abstract class DefaultBehaviourAoe extends AbstractEntityProperty {
    protected AoeCloud aoeCloud;
    public abstract TagModifierHelper getTag();
    public abstract void onTickMethod();
    public abstract AbstractElement getType();
    public void getEntity(AoeCloud aoeCloud){ this.aoeCloud = aoeCloud; }

    public void discardCondition(){}

    public void addAdditionalDetails(CompoundTag compoundTag){}

    public void readCompoundTag(CompoundTag compoundTag){}
}
