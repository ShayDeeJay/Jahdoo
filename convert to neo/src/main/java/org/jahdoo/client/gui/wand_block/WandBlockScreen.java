package org.jahdoo.client.gui.wand_block;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.client.SharedUI;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.tags.TagHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.client.SharedUI.drawStringWithBackground;
import static org.jahdoo.items.augments.AugmentItemHelper.getAllAbilityModifiers;
import static org.jahdoo.items.augments.AugmentItemHelper.shiftForDetails;

public class WandBlockScreen extends AbstractContainerScreen<WandBlockMenu> {
    WandBlockMenu wandBlockMenu;
    public static final int IMAGE_SIZE = 256;
    private double scrollX;
    private double scrollY;
    long window = Minecraft.getInstance().getWindow().getWindow();
    Options settings = Minecraft.getInstance().options;

    public WandBlockScreen(WandBlockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.wandBlockMenu = pMenu;
        this.width = 50;
        this.height = 50;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {}

    private List<AbstractAbility> getAbilityFromRegistry(ItemStack comparable){
        ResourceLocation resourceLocation = TagHelper.getAbilityTypeItemStackResource(comparable);

        return AbilityRegister.getSpellsByTypeId(comparable.get(DataComponentRegistry.GET_ABILITY_KEY.get()));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}

    private ItemStack getMatchingItem(ItemStack comparable){

        for(int i = 1; i < this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getSlots(); i++){
            ItemStack currentAugment = this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getStackInSlot(i);
            List<AbstractAbility> selectedAbility = this.getAbilityFromRegistry(comparable);

            if(!selectedAbility.isEmpty()){
                WandAbilityHolder wandAbilityHolder = currentAugment.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());

                if (wandAbilityHolder != null && wandAbilityHolder.abilityProperties().containsKey(selectedAbility.get(0).setAbilityId())) {
                    return currentAugment;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private int getMatchingIndex(ItemStack comparable){
        if(comparable.is(ItemsRegister.AUGMENT_ITEM.get()) && TagHelper.hasWandAbilitiesTag(comparable)){
            for (int i = 1; i < this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getSlots(); i++) {
                ItemStack currentAugment = this.wandBlockMenu.getWandBlockEntity().inputItemHandler.getStackInSlot(i);
                List<AbstractAbility> ability = this.getAbilityFromRegistry(comparable);
                List<AbstractAbility> currentAbility = this.getAbilityFromRegistry(currentAugment);

                if(!ability.isEmpty() && !currentAbility.isEmpty()){
                    WandAbilityHolder wandAbilityHolder = currentAugment.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());


                    if (wandAbilityHolder.abilityProperties().containsKey(ability.get(0).setAbilityId())) {
                        return i - 1;
                    }
                }
            }
        }
        return -1;
    }

    private Component getAbilityName(ItemStack itemStack, AbstractElement info){
        ResourceLocation getAbilityResource = TagHelper.getAbilityTypeItemStackResource(itemStack);
        List<AbstractAbility> ability = AbilityRegister.getSpellsByTypeId(itemStack.get(DataComponentRegistry.GET_ABILITY_KEY.get()));

        if(ability.isEmpty()) return Component.empty();
        return Component.literal(ability.get(0).getAbilityName()).withStyle((style) -> style.withColor(info.textColourPrimary()));
    }

    public  List<Component> getHoverText(ItemStack itemStack, ItemStack itemStack1){
        List<Component> toolTips = new ArrayList<>();
        if(itemStack.getComponents().isEmpty()) return toolTips;

        String abilityLocation = itemStack.get(DataComponentRegistry.GET_ABILITY_KEY.get());
        CustomModelData getElement = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);

        if(getElement != null){
            List<AbstractElement> info = ElementRegistry.getElementByTypeId(Math.max(getElement.value(), 0));

            if (itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()) != null && !info.isEmpty()) {
                toolTips.add(
                    Component.literal("Ability: ")
                        .withStyle(style -> style.withColor(-9013642))
                        .append(this.getAbilityName(itemStack, info.get(0)))
                );
                toolTips.add(Component.empty());
                getAllAbilityModifiers(itemStack, itemStack1, toolTips, abilityLocation);
                shiftForDetails(toolTips);
            }
        }

        return toolTips;
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int pX, int pY) {
        if(this.hoveredSlot == null || !InputConstants.isKeyDown(window, settings.keyShift.getKey().getValue())) return;
        ItemStack matchedItem = this.getMatchingItem(this.hoveredSlot.getItem());
        List<Component> getTooltipMatched = getHoverText(matchedItem, this.hoveredSlot.getItem());
        List<Component> getTooltipHovered = getHoverText(this.hoveredSlot.getItem(), matchedItem);

        if(getTooltipMatched.isEmpty() || this.hoveredSlot.index >= 36) {
            super.renderTooltip(
                guiGraphics,
                this.hoveredSlot.index > 41 ? pX - ((this.getMaxLengthItem(getTooltipMatched) * 5) + 20) : pX,
                pY + (this.hoveredSlot.index == 36 ? -70 : 20)
            );
            return;
        }

        guiGraphics.renderTooltip(
            this.font, getTooltipHovered,
            this.hoveredSlot.getItem().getTooltipImage(),
            this.hoveredSlot.getItem(),
            this.hoveredSlot.index > 41 ? pX - ((this.getMaxLengthItem(getTooltipMatched) * 5) + 20) : pX,
            pY + (this.hoveredSlot.index == 36 ? -70 : 20)
        );

        boolean isValidItem = this.hoveredSlot.getItem().is(ItemsRegister.AUGMENT_ITEM.get());
        boolean isValidSlot = this.hoveredSlot.index < 36;

        if (!isValidItem || !isValidSlot) return;

        if (!matchedItem.isEmpty()) {
            getTooltipMatched.add(1, Component.literal("Equipped").withStyle(style -> style.withColor(-8660735)));

            guiGraphics.renderTooltip(
                this.font,
                getTooltipMatched,
                matchedItem.getTooltipImage(),
                matchedItem,
                pX - ((this.getMaxLengthItem(getTooltipMatched) * 5) + 20),
                pY + 20
            );

        }
    }

    private int getMaxLengthItem(List<Component> components){
        AtomicInteger getMaxLength = new AtomicInteger();
        components.forEach(
            component -> {
                if(component.getString().length() > getMaxLength.get()) getMaxLength.set(component.getString().length());
            }
        );
        return getMaxLength.get();
    }

    private void setSlotTexturesGrid(GuiGraphics guiGraphics){
        SharedUI.handleSlotsInGridLayout(
            (slotX, slotY, index) -> {
                int slotX1 = slotX - 32 / 2;
                int slotY1 = slotY - 32 / 2;

                adjustAllSlotRelated(guiGraphics, slotX - 208, slotY - 223, index, slotX1, slotY1);
            },
            this.wandBlockMenu.getWandBlockEntity().getAllowedSlots(),
            this.width,
            this.height,
            wandBlockMenu.xSpacing,
            wandBlockMenu.ySpacing
        );
    }

    private void setSlotTexture(GuiGraphics guiGraphics, int slotX, int slotY, int i){
        guiGraphics.blit(GeneralHelpers.modResourceLocation("textures/gui/slot.png"), slotX, slotY - 3, 0, 0, IMAGE_SIZE, IMAGE_SIZE);
    }

    private void adjustAllSlotRelated(GuiGraphics guiGraphics, int slotX, int slotY, int i, int slotX1, int slotY1){
        int adjustGroupY = -1;
        // Set the slot texture and hovered border

        this.setSlotTexture(
            guiGraphics,
            (int) (slotX + scrollX) + wandBlockMenu.xOffset,
            (int) (slotY + scrollY) + wandBlockMenu.yOffset + adjustGroupY, i
        );

        this.setSlotHoveredBorder(
            guiGraphics,
            (int) (slotX + scrollX) + wandBlockMenu.xOffset,
            (int) (slotY + scrollY) + wandBlockMenu.yOffset + adjustGroupY,
            slotX1, slotY1, i
        );

        drawStringWithBackground(
            guiGraphics,
            this.getMinecraft().font,
            Component.literal(String.valueOf(i + 1)),
            slotX + 128, slotY + 109 + wandBlockMenu.yOffset + adjustGroupY,
            -14145496, -6250336,
            true
        );
    }


    private void setSlotHoveredBorder(GuiGraphics guiGraphics, int x, int y, int slotX1, int slotY1, int i){
        boolean conditionOne = this.hoveredSlot != null && this.hoveredSlot.index - 36 == i ;
        boolean conditionTwo = this.hoveredSlot != null && this.getMatchingIndex(this.hoveredSlot.getItem()) == i ;


        if(conditionOne || conditionTwo){
            guiGraphics.blit(this.getResourceByType(0), x, y - 3, 0, 0, IMAGE_SIZE, IMAGE_SIZE);
            if(this.hoveredSlot.index < 36){
                int imageWidth = 32;
                int imageHeight = 32;

                int slotX = this.hoveredSlot.x;
                int slotY = this.hoveredSlot.y;

                int drawX = slotX + (width / 2) - (imageWidth / 2);
                int drawY = slotY + (height / 2) - (imageHeight / 2);

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0,0,280);

                guiGraphics.blit(
                    GeneralHelpers.modResourceLocation("textures/gui/in_wand.png"),
                    (int) (slotX1 + scrollX) - 80,
                    (int) (slotY1 - 64 + scrollY),
                    0, 0, imageWidth, imageHeight, imageWidth, imageHeight
                );

                guiGraphics.blit(
                    GeneralHelpers.modResourceLocation("textures/gui/in_inventory.png"),
                    (int) (drawX - 80 + scrollX),
                    (int) (drawY - 83 + scrollY),
                    0, 0, imageWidth, imageHeight, imageWidth, imageHeight
                );

                guiGraphics.pose().popPose();
            }
        }
    }

    private void overlayInventoryWithHighlightedType(GuiGraphics guiGraphics, int x, int y){
        guiGraphics.blit(
            this.getResourceByType(1),
            (int) (x + this.scrollX),
            (int) (y + this.scrollY) + wandBlockMenu.yOffset - 44,
            0, 0, IMAGE_SIZE, IMAGE_SIZE
        );
    }

    private ResourceLocation getResourceByType(int renderType) {
        List<String> paths = List.of(
            "textures/gui/slot_selected_%s.png",
            "textures/gui/gui_overlay_%s.png"
        );
        String texturePaths = String.format(paths.get(renderType), "empty");
        if(this.hoveredSlot != null && this.hoveredSlot.getItem().is(ItemsRegister.AUGMENT_ITEM.get())){

            if (this.hoveredSlot.getItem().get(DataComponents.CUSTOM_MODEL_DATA) == null) GeneralHelpers.modResourceLocation(texturePaths);

            CustomModelData selectedIndex = this.hoveredSlot.getItem().get(DataComponents.CUSTOM_MODEL_DATA);
            if(selectedIndex != null){
                List<AbstractElement> getElements = ElementRegistry.getElementByTypeId(selectedIndex.value());

                if (!getElements.isEmpty()) {
                    String getOrDefault = ElementRegistry.getElementByTypeId(selectedIndex.value()).get(0).setAbilityId();
                    String texturePath = String.format(paths.get(renderType), getOrDefault);
                    return GeneralHelpers.modResourceLocation(texturePath);
                }
            }
        }

        return GeneralHelpers.modResourceLocation(texturePaths);
    }

    private void abilityIcon(GuiGraphics guiGraphics){
        int horizontalOffset = -71;
        int verticalOffset = 40 + wandBlockMenu.yOffset;
        int localImageSize = 40;
        int shrinkBy = 16;
        int imageWithShrink = localImageSize - shrinkBy;
        int posX = (width - localImageSize) / 2 + horizontalOffset;
        int posY = (height - localImageSize) / 2 - 150 + verticalOffset;
        int posX1 = (width - imageWithShrink) / 2 + horizontalOffset;
        int posY1 = (height - imageWithShrink) / 2 - 150 + verticalOffset;

        guiGraphics.blit(
            GeneralHelpers.modResourceLocation("textures/gui/gui_button.png"),
            posX, posY, 0, 0, localImageSize, localImageSize, localImageSize, localImageSize
        );

        guiGraphics.blit(
            GeneralHelpers.modResourceLocation("textures/gui/title_bar.png"),
            posX + 35, posY + 5, 0, 0, 120, 32, 120, 32
        );

        if(this.hoveredSlot == null) return;
        List<AbstractAbility> abstractAbility = this.getAbilityFromRegistry(this.hoveredSlot.getItem());


        if(!abstractAbility.isEmpty()){
            if (!abstractAbility.get(0).getAbilityIconLocation().getPath().isEmpty()) {
                this.getAbilityName(abstractAbility.get(0), guiGraphics);

                if(abstractAbility.get(0).getAbilityIconLocation() != null){
                    guiGraphics.blit(
                        abstractAbility.get(0).getAbilityIconLocation(),
                        posX1, posY1, 0, 0, imageWithShrink, imageWithShrink, imageWithShrink, imageWithShrink
                    );
                }
            }
        }
    }

    public void getAbilityName(AbstractAbility abstractAbility, GuiGraphics guiGraphics){
        int textX = (width/2) - 43;
        int textY = (height/2) - 113 + wandBlockMenu.yOffset;
        if(this.hoveredSlot == null) return;

        int colour = SharedUI.getAbilityNameWithColour(abstractAbility, this.hoveredSlot.getItem());
        Component component = Component.literal(abstractAbility.getAbilityName());
        SharedUI.drawStringWithBackground(guiGraphics, this.font, component, textX, textY, -14013910, colour, false);

    }

    public void renderInventoryBackground(GuiGraphics guiGraphics){
        int x = (width - IMAGE_SIZE) / 2;
        int y = (height - IMAGE_SIZE) / 2;
//        guiGraphics.enableScissor(100, 100, 400, 500);
//        guiGraphics.pose().pushPose();

        guiGraphics.blit(
            GeneralHelpers.modResourceLocation("textures/gui/wand_gui.png"),
            (int) (x + scrollX),
            (int) (y + scrollY) + wandBlockMenu.yOffset - 44,
            0,0, IMAGE_SIZE, IMAGE_SIZE
        );

//        guiGraphics.disableScissor();
//        guiGraphics.pose().popPose();
    }

//    @Override
//    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
//        this.scrollY += (int) pDelta * 4;
//        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
//    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private void setCustomBackground(GuiGraphics guiGraphics){
        int width = this.width/2;
        int height = this.height/2;
        int widthOffset = 100;
        int heightOffset = 115;
        int widthFrom = width - widthOffset;
        int heightFrom = height - heightOffset;
        int widthTo = width + widthOffset;
        int heightTo = height + heightOffset;

        int fromColour = -1072689136;
        int toColour = -804253680;

        guiGraphics.fillGradient(widthFrom, heightFrom, widthTo, heightTo, fromColour, toColour);
    }


    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pPartialTick) {
        if(this.hoveredSlot != null){
            CustomModelData customModelData = this.hoveredSlot.getItem().get(DataComponents.CUSTOM_MODEL_DATA);
            WandAbilityHolder wandAbilityHolder = this.hoveredSlot.getItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            String string = this.hoveredSlot.getItem().get(DataComponentRegistry.GET_ABILITY_KEY.get());

//            System.out.println(string);

            if(wandAbilityHolder != null){
//                System.out.println(wandAbilityHolder.abilityProperties());
            }

            if(customModelData != null){
//                System.out.println(customModelData.value());
            }
        }
        this.renderBlurredBackground(pPartialTick);
        this.setCustomBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, pPartialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - IMAGE_SIZE) / 2;
        int y = (height - IMAGE_SIZE) / 2;

        this.renderInventoryBackground(guiGraphics);
        this.overlayInventoryWithHighlightedType(guiGraphics, x, y);
        this.abilityIcon(guiGraphics);
        this.setSlotTexturesGrid(guiGraphics);
    }
}
