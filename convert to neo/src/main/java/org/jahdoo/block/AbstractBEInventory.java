package org.jahdoo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.particle.ParticleStore.rgbToInt;

public abstract class AbstractBEInventory extends BlockEntity {

    public static GenericParticleOptions processingParticle(int lifetime, float size, boolean staticSize, double speed){
        return new GenericParticleOptions(
            ParticleStore.SOFT_PARTICLE_SELECTION,
            rgbToInt(40, 134, 110),
            rgbToInt(171, 219, 207),
            lifetime, size, staticSize, speed
        );
    }

//    protected LazyOptional<IItemHandler> inputLazyItemHandler = Lazy.empty();
//    protected LazyOptional<IItemHandler> outputLazyItemHandler = LazyOptional.empty();
    protected ContainerData data;

    public AbstractBEInventory(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int stackSize) {
        super(pType, pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return 0;
            }

            @Override
            public void set(int pIndex, int pValue) {}

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public final ItemStackHandler inputItemHandler = new ItemStackHandler(setInputSlots()) {
        protected void onContentsChanged(int slot) {
            sendBlockUpdate(level, () -> setChanged(), getBlockPos(), getBlockState());
        }
    };

    public final ItemStackHandler outputItemHandler = new ItemStackHandler(setOutputSlots()){
        protected void onContentsChanged(int slot) {
            sendBlockUpdate(level, () -> setChanged(), getBlockPos(), getBlockState());
        }
    };

    public abstract int setInputSlots();
    public abstract int setOutputSlots();
    public abstract int getMaxSlotSize();

    public ContainerData getData() {
        return data;
    }

//    @Override
//    public void onLoad() {
//        super.onLoad();
//        inputLazyItemHandler = LazyOptional.of(() -> inputItemHandler);
//        outputLazyItemHandler = LazyOptional.of(() -> outputItemHandler);
//
//    }
//
//    @Override
//    public void invalidateCapabilities() {
//        super.invalidateCapabilities();
//        inputLazyItemHandler.invalidate();
//        outputLazyItemHandler.invalidate();
//    }

    public void dropsAllInventory(Level level) {
        SimpleContainer inputInventory = new SimpleContainer(setInputSlots());
        SimpleContainer outputInventory = new SimpleContainer(setInputSlots());

        for(int i = 0; i < this.inputItemHandler.getSlots(); i++) {
            inputInventory.setItem(i, inputItemHandler.getStackInSlot(i));
        }

        for(int i = 0; i < this.outputItemHandler.getSlots(); i++) {
            outputInventory.setItem(i, outputItemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, this.worldPosition, inputInventory);
        Containers.dropContents(level, this.worldPosition, outputInventory);
    }

//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @javax.annotation.Nullable Direction side) {
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            if(side == Direction.DOWN) {
//                return outputLazyItemHandler.cast();
//            }
//            return inputLazyItemHandler.cast();
//        }
//        return super.getCapability(cap, side);
//    }



    public static void sendBlockUpdate(Level level, Runnable setChanged, BlockPos blockPos, BlockState blockState){
        if(level == null) return;
        setChanged.run();
        level.sendBlockUpdated(blockPos, blockState, blockState,3);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inputInventory", inputItemHandler.serializeNBT(pRegistries));
        pTag.put("outputInventory", outputItemHandler.serializeNBT(pRegistries));
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        inputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("inputInventory"));
        outputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("outputInventory"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

}
