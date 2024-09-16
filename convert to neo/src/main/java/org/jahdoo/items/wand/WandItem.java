package org.jahdoo.items.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashMap;
import java.util.List;

import static org.jahdoo.block.wand.WandBlockEntity.GET_WAND_SLOT;

public class WandItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation CAST_TWO = RawAnimation.begin().thenPlayAndHold("cast_two");
    public String location;

    public static Properties wandProperties(){
        Properties properties = new Properties();
        properties.stacksTo(1);
        properties.component(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), new WandAbilityHolder(new HashMap<>(4)));
        properties.fireResistant();
        return properties;
    }

    public WandItem(String location) {
        super(BlocksRegister.WAND.get(), wandProperties());
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
        this.location = location;
    }


    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 72000;
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        Player player = pContext.getPlayer();

        if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;

        BlockPos clickedPos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        ItemStack itemStack = player.getMainHandItem();
        level.setBlockAndUpdate(clickedPos, BlocksRegister.WAND.get().defaultBlockState());
        BlockEntity blockEntity = level.getBlockEntity(clickedPos);

        if (blockEntity instanceof WandBlockEntity wandBlockEntity) {
            playPlaceSound(level, pContext.getClickedPos());
            ItemStack copiedWand = itemStack.copyWithCount(1);
            wandBlockEntity.inputItemHandler.setStackInSlot(GET_WAND_SLOT, copiedWand);
            wandBlockEntity.updateView();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return SoundEvents.EMPTY;
    }

    public void playPlaceSound(Level level, BlockPos bPos){
        SoundEvent soundEvents = SoundEvents.BEACON_ACTIVATE;
        GeneralHelpers.getSoundWithPosition(level, bPos, soundEvents, 0.4f, 1.5f);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSoltSelected) {
        if(itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()) == null){
            itemStack.set(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), new WandAbilityHolder(new HashMap<>()));
        }

        if(!(entity instanceof Player player)) return;
        if (player.getItemInHand(player.getUsedItemHand()) == itemStack) {}
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.addAll(WandItemHelper.getItemModifiers(pStack));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return WandItemHelper.getItemName(pStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        WandSlotManager.getTotalSlots(player.getMainHandItem());

        var linkedHashMap = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()).abilityProperties()   ;
//        System.out.println(linkedHashMap.keySet());

        if (interactionHand == InteractionHand.MAIN_HAND) {
            if (player instanceof ServerPlayer serverPlayer) {
                return CastHelper.use(serverPlayer, interactionHand);
            }
        }

        return InteractionResultHolder.fail(player.getMainHandItem());
    }



    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return super.getDefaultAttributeModifiers(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtil.getCurrentTick();
    }
}
