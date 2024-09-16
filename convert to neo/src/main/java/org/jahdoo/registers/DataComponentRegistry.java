package org.jahdoo.registers;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.Components.AbilityHolder;
import org.jahdoo.Components.WandAbilityHolder;
import org.jahdoo.JahdooMod;

import java.util.function.UnaryOperator;

public class DataComponentRegistry {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JahdooMod.MOD_ID);



    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AbilityHolder>> ABILITY_HOLDER =
        register("ability_holder", (builder) -> builder.persistent(AbilityHolder.CODEC).networkSynchronized(AbilityHolder.STREAM_CODEC).cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandAbilityHolder>> WAND_ABILITY_HOLDER =
        register("wand_ability_holder", builder -> builder.persistent(WandAbilityHolder.CODEC).networkSynchronized(WandAbilityHolder.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> AUGMENT_RATING =
        register("augment_rating", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ABILITY_SLOTS =
        register("ability_slots", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> GET_ABILITY_KEY =
        register("jahdoo_ability_type", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> HEART_CONTAINER =
        register("heart_container", builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));


    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator) {
        return COMPONENTS.register(name, () -> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
