package org.jahdoo.client.gui.ability_and_utility_menus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ScreenButton extends ImageButton {
    private float sizes;
    private final int defaultSize;
    private final int totalSize;
    private final OnPress pOnPress;
    private final int defaultX;
    private final int defaultY;
    private final int getButtonId;

    public ScreenButton(int pX, int pY, WidgetSprites sprites, int size, OnPress pOnPress, int getButtonId) {
        super(pX, pY, size, size, sprites, pOnPress);
        this.defaultSize = size; // Default size is the initial size of the button
        this.sizes = size; // Initialize sizes to the default size
        this.totalSize = size + 30; // Increased size
        this.pOnPress = pOnPress;
        this.defaultX = pX;
        this.defaultY = pY;
        this.getButtonId = getButtonId;
    }

    public float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        float normalizedTick = (sizes - defaultSize) / (totalSize - defaultSize);

        float easedTick = easeInOutCubic(normalizedTick);

        int easedValue = (int) (easedTick * (totalSize - defaultSize)) + defaultSize;

        int offset = (easedValue - defaultSize) / 2;

        if (this.isMouseOver(pMouseX, pMouseY)) {
            sizes = Math.min(sizes + 2f, totalSize);
            pOnPress.onPress(this);
            this.clicked(pMouseX, pMouseY);
        } else {
            if (sizes > defaultSize) sizes -= 2f;
        }

        pGuiGraphics.blit(this.sprites.enabled(), this.getX() - offset, this.getY() - offset, 0, 0, 0, easedValue, easedValue, easedValue, easedValue);

    }

    @Override
    public void onPress() {
        Minecraft.getInstance().setScreen(null);
    }
}
