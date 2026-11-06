package org.antarcticgardens.cna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.UnaryOperator;

/** PORT-NOTE(26.1): was a NeoForge DeferredRegister.DataComponents; registered directly. */
public class CNADataComponents {
    public static final DataComponentType<BlockPos> BOUND_TO = register(
            "bound_to",
            builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CreateNewAge.asResource(name),
                builder.apply(DataComponentType.builder()).build());
    }

    public static void load() {
    }
}
