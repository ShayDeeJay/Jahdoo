package org.jahdoo.capabilities;

import net.minecraft.nbt.CompoundTag;

public interface AbstractCapability {
    void saveNBTData(CompoundTag nbt);
    void loadNBTData(CompoundTag nbt);
    void copyFrom(AbstractCapability source);
}
