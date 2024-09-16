package org.jahdoo.client.gui.mana_ability_overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.tags.TagHelper;

public class ManaBarOverlay implements LayeredDraw.Layer {
    float fadeIn;
    public static final ResourceLocation MANA_GUI = GeneralHelpers.modResourceLocation("textures/gui/mana_v4_textured.png");


    @Override
    public void render(GuiGraphics pGuiGraphics, DeltaTracker pDeltaTracker) {
        Player player = Minecraft.getInstance().player;
        Screen screen = Minecraft.getInstance().screen;
        AlignedGui alignedGui = new AlignedGui(pGuiGraphics, screen.height, screen.width);
        AbstractAbility abstractAbility = AbilityRegister.REGISTRY.get(TagHelper.getAbilityTypeWand(player));
        alignedGui.offsetGui(1,1);

        if (player != null) {

            int manaBarWidth = 57;

            if(player.getMainHandItem().getItem() instanceof WandItem) {
                if (fadeIn < 1) fadeIn += 0.05f;
            } else {
                if(this.fadeIn > 0) fadeIn -= 0.05f;
            }

//            ManaSystem manaSystem = getManaSystem(player);
//            AbilityCooldownSystem jahdooCooldown = getCooldownSystem(player);
//            int manaProgress = manaSystem.getMaxMana() != 0 && manaSystem.getManaPool() != 0 ? (int) (manaSystem.getManaPool() * manaBarWidth / manaSystem.getMaxMana()) : 0;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            pGuiGraphics.pose().pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fadeIn);
            pGuiGraphics.pose().translate(0, -fadeIn, 0);

            //Container
            alignedGui.displayGuiLayer(1, 29, 120, 89, 29);
            //Mana bar
//            alignedGui.displayGuiLayer(28, 18, 111, manaProgress, 8);
            //Wand level bar
            alignedGui.displayGuiLayer(40, 25, 108, 52, 3);

            if(abstractAbility != null ){
                alignedGui.displayGuiLayer(4, 26, 0, 0, 23, abstractAbility.getAbilityIconLocation());
//                if (jahdooCooldown != null && jahdooCooldown.isAbilityOnCooldown(abstractAbility.setAbilityId())) {
//                    ListTag getCooldownCost = TagHelper.getWandAbilities(abstractAbility.setAbilityId(), player).getList(COOLDOWN, Tag.TAG_DOUBLE);
                    // need to change this, need to find the charged amount that was set as this can be lowered when modified
//                    double totalCooldownCost = getCooldownCost.getDouble(0);
//                    double getCurrentCooldown = jahdooCooldown.getCooldown(abstractAbility.setAbilityId());
//                    int cooldownOverlaySize = 19;
//                    int currentOverlayHeight = (int) ((getCurrentCooldown) * cooldownOverlaySize / totalCooldownCost);
//                    Cooldown overlay
//                    alignedGui.displayGuiLayer(6, 24, 89, 19, currentOverlayHeight);
                }
            }


//            drawStringWithBackground(pGuiGraphics, Minecraft.getInstance().font, Component.literal(String.valueOf(Math.round(manaSystem.getManaPool()))), 58, screen.height - 25,-13816531, -13716253, true);

            RenderSystem.disableBlend();
            pGuiGraphics.pose().popPose();

    }

    public static class AlignedGui {
        GuiGraphics guiGraphics;
        private int shiftGuiX;
        private int shiftGuiY;
        private final int screenWidth;
        private final int screenHeight;

        public AlignedGui(GuiGraphics guiGraphics, int screenHeight, int screenWidth){
            this.guiGraphics = guiGraphics;
            this.screenHeight = screenHeight;
            this.screenWidth = screenWidth;
        }

        public void displayGuiLayer(int xA, int yA, int offsetY, int barSizeXb, int barSizeYb){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(MANA_GUI, positionX, positionY, 77, offsetY, barSizeXb, barSizeYb);
        }

        public void displayGuiLayer(int xA, int yA, int offsetX, int offsetY, int iconSize, ResourceLocation resourceLocation){
            int positionX = xA + shiftGuiX;
            int positionY = screenHeight - yA - shiftGuiY;
            guiGraphics.blit(resourceLocation, positionX, positionY, offsetX, offsetY, iconSize, iconSize, iconSize, iconSize);
        }

        public void offsetGui(int shiftGuiX, int shiftGuiY){
            this.shiftGuiX = shiftGuiX;
            this.shiftGuiY = shiftGuiY;
        }
    }

}
