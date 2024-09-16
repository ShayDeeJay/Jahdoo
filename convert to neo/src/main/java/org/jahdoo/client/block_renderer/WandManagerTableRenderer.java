package org.jahdoo.client.block_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.block.wandBlockManager.WandManagerTableBlock;
import org.jahdoo.block.wandBlockManager.WandManagerTableEntity;
import org.jahdoo.items.wand.WandItem;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.items.wand.WandItemHelper.getItemModifiers;
import static org.jahdoo.items.wand.WandItemHelper.getItemName;

public class WandManagerTableRenderer implements BlockEntityRenderer<WandManagerTableEntity> {
    private final BlockEntityRenderDispatcher entityRenderDispatcher;
    public WandManagerTableRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getBlockEntityRenderDispatcher();
    }

    private int direction(BlockEntity blockEntity){
        Direction direction = blockEntity.getBlockState().getValue(WandManagerTableBlock.FACING);

        if(direction == Direction.SOUTH ) return 0;
        if(direction == Direction.WEST ) return 90;
        if(direction == Direction.NORTH ) return 180;
        return 270;
    }

    @Override
    public void render(
        WandManagerTableEntity wandManagerTable,
        float pPartialTick,
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        int pPackedOverlay
    ){
//        this.renderNameTag(wandManagerTable, Component.literal("Requires 20XP levels to craft"), pPoseStack, pBuffer, pPackedLight);
        wandManagerTable.setAnimator(wandManagerTable.getAnimationTickerIncrement());
        wandManagerTable.setAnimatedDistance();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack outputSlot = wandManagerTable.inputItemHandler.getStackInSlot(0);
        float f1 = Minecraft.getInstance().options.getBackgroundOpacity(1F);
        int alpha = (int)(f1 * 255.0F) << 24;
        int blue = 0x4F442D;
        int argbColor = alpha | blue;
        float f = (float)(argbColor >> 24 & 255) / 255.0F;
        float f12 = (float)(argbColor >> 16 & 255) / 255.0F;
        float f2 = (float)(argbColor >> 8 & 255) / 255.0F;
        float f3 = (float)(argbColor & 255) / 255.0F;

        AtomicInteger atomicInteger = new AtomicInteger();

        if(outputSlot.getItem() instanceof WandItem){
            List<Component> modlist = getItemModifiers(outputSlot);

            this.renderNameTag(wandManagerTable, getItemName(outputSlot), pPoseStack, pBuffer, pPackedLight, 1.15);

            for(int i = 0; i < modlist.size(); i++){
                int backward = modlist.size() - (i+1);
                if (!modlist.get(backward).getString().isEmpty()) {
                    this.renderNameTag(wandManagerTable, modlist.get(backward), pPoseStack, pBuffer, pPackedLight, (double) i / 15);
                }
            }

            for(int i = 0; i < modlist.size(); i++){
                this.renderBackground(wandManagerTable, Component.literal("                    "), pPoseStack, pBuffer, pPackedLight, (double) i/10);
            }

        }
        focusedItem(pPoseStack, wandManagerTable, itemRenderer, pBuffer, pPartialTick);
//        for(int i = 0; i < wandManagerTable.inputItemHandler.getSlots(); i++){
//            ItemStack itemStack = wandManagerTable.inputItemHandler.getStackInSlot(i);
//            if(!itemStack.isEmpty()){
//                atomicInteger.set(atomicInteger.get() + 1);
//            }
//        }
//
//        for(int i = 0; i < wandManagerTable.inputItemHandler.getSlots(); i++){
//            ItemStack itemStack = wandManagerTable.inputItemHandler.getStackInSlot(i);
//
//            int finalI = i;
//            rotateAllItems(pPoseStack, wandManagerTable, () -> rotateItem(pPoseStack, wandManagerTable, itemRenderer, itemStack, pBuffer, atomicInteger.get(), finalI), i, atomicInteger.get());
//        }

    }

    private void focusedItem(PoseStack pPoseStack,  WandManagerTableEntity pBlockEntity, ItemRenderer itemRenderer, MultiBufferSource pBuffer, float partialTicks){
        pPoseStack.pushPose();
        float getCurrentTime = (float) pBlockEntity.animationTicker;
        float scaleItem = 0.80f;
        ItemStack outputSlot = pBlockEntity.inputItemHandler.getStackInSlot(0);

//        this.renderNameTag(pBlockEntity, Component.literal("im here"), pPoseStack,pBuffer,100);



        pPoseStack.translate(0.5f,  Math.sin(getCurrentTime / 50.0F) * 0.02F + 1.15f, 0.5f);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);

        pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(direction(pBlockEntity)));

        ItemStack getItem = outputSlot.isEmpty() ? ItemStack.EMPTY : outputSlot;

        itemRenderer.renderStatic(
            getItem,
            ItemDisplayContext.FIXED,
            getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );

        pPoseStack.popPose();
    }

    private void rotateAllItems(PoseStack pPoseStack, WandManagerTableEntity pBlockEntity, Runnable stuff, int index, int totalItems){
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0, 0.5f);
        float angleOffset = 360.0f / totalItems;
        float itemAngle = angleOffset * index;
        pPoseStack.mulPose(Axis.YP.rotationDegrees(itemAngle + (float) pBlockEntity.animationTicker));
        stuff.run();
        pPoseStack.popPose();
    }


    private void rotateItem(PoseStack pPoseStack, WandManagerTableEntity pBlockEntity, ItemRenderer itemRenderer, ItemStack itemStack, MultiBufferSource pBuffer, int totalItems, float pPartialTicks){
        pPoseStack.pushPose();
        float getCurrentTime = (float) pBlockEntity.animationTicker;
        float scaleItem = 0.3f;

        pPoseStack.translate(0, 1.25f /*+ bobbing*/, pBlockEntity.animateDistanceIncrement/5);
        pPoseStack.scale(scaleItem, scaleItem, scaleItem);
        if(!pBlockEntity.canCraftWand()){
            pPoseStack.mulPose(Axis.YP.rotationDegrees(getCurrentTime));
        }

        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.getLevel(),
            1
        );
        pPoseStack.popPose();


    }


    protected void renderNameTag(BlockEntity blockEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, double yOffset) {
        BlockPos cameraPosition = this.entityRenderDispatcher.camera.getBlockPosition();
        BlockPos entityPosition = blockEntity.getBlockPos();
        boolean blockDistanceVisible = entityPosition.closerToCenterThan(cameraPosition.getCenter(), 5);

        if (blockDistanceVisible) {
            pPoseStack.pushPose();
            pPoseStack.translate(-0.15, 1.35f + yOffset, 0.05F);
            float scale = 0.005f;
//            pPoseStack.mulPose(this.entityRenderDispatcher.camera.rotation());
            //For static view, double up for double sided view
            pPoseStack.mulPose(Axis.YP.rotationDegrees(direction(blockEntity) - 90));
            pPoseStack.scale(-scale, -scale, scale);
            Matrix4f matrix4f = pPoseStack.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.5F);
            int j = (int)(f1 * 255.0F) << 24;
            Font font = Minecraft.getInstance().font;
            float f2 = (float)(-font.width(pDisplayName) / 2);
            font.drawInBatch(pDisplayName, 0, 1, -1, false, matrix4f, pBuffer, Font.DisplayMode.NORMAL, 0, 255);
            pPoseStack.popPose();
        }
    }

    protected void renderBackground(BlockEntity blockEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, double yOffset) {
        BlockPos cameraPosition = this.entityRenderDispatcher.camera.getBlockPosition();
        BlockPos entityPosition = blockEntity.getBlockPos();
        boolean blockDistanceVisible = entityPosition.closerToCenterThan(cameraPosition.getCenter(), 5);

        if (blockDistanceVisible) {
            pPoseStack.pushPose();
            pPoseStack.translate(-0.19, 1.3f + yOffset, 0.05F);
            float scale = 0.01f;
//            pPoseStack.mulPose(this.entityRenderDispatcher.camera.rotation());
            //For static view, double up for double sided view
            pPoseStack.mulPose(Axis.YP.rotationDegrees(direction(blockEntity) - 90));
            pPoseStack.scale(-scale, -scale, scale);
            Matrix4f matrix4f = pPoseStack.last().pose();

            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.85F);
            int alpha = (int)(f1 * 255.0F) << 24;
            int blue = 0x3B3B3B;
            int argbColor = alpha | blue;

            Font font = Minecraft.getInstance().font;
            float f2 = (float)(-font.width(pDisplayName) / 2);
            font.drawInBatch(pDisplayName, 0, 1, -1, false, matrix4f, pBuffer, Font.DisplayMode.NORMAL, argbColor, 255);
            pPoseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos blockPos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int sLight = level.getBrightness(LightLayer.SKY, blockPos);
        return LightTexture.pack(bLight, sLight);
    }
}