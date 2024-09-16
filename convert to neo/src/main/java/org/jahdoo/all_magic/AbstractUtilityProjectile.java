package org.jahdoo.all_magic;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.tags.TagModifierHelper;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;

public class AbstractUtilityProjectile extends DefaultEntityBehaviour {

    @Override
    public TagModifierHelper getTag() {
        return null;
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.UTILITY.get();
    }

    @Override
    public void onTickMethod() {
        if(this.genericProjectile != null){
            ParticleHandlers.GenericProjectile(this.genericProjectile,
                new BakedParticleOptions(getElementType().getTypeId(), 2, 0.25f, true),
                genericParticleOptions(this.getElementType(), 5, 1f),
                0.015
            );
        }
    }

    @Override
    public void discardCondition() {
        if(this.genericProjectile != null){
            if (this.genericProjectile.tickCount > 300) this.genericProjectile.discard();
        }
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return null;
    }

}
