package org.jahdoo.client.block_models;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.JahdooMod;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.utils.GeneralHelpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class InfuserModel extends DefaultedBlockGeoModel<InfuserBlockEntity> {
    public InfuserModel() {
        super(GeneralHelpers.modResourceLocation("infuser"));
    }

    @Override
    public RenderType getRenderType(InfuserBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
