/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.port.esl.ESL;
import org.antarcticgardens.cna.port.esl.fabric.energy.E2FEnergyStorageAdapter;
import org.antarcticgardens.cna.port.esl.fabric.energy.F2EEnergyStorageAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/** PORT-NOTE(26.1): FabricBlockEnergyStorageManager merged in (registers with TR Energy's {@code EnergyStorage.SIDED}). */
public class BlockEnergyStorageManager {
    private final Map<BlockEntityType<? extends BlockEntity>, BiFunction<BlockEntity, Direction, EnergyStorage>> blockEntityProviders = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> void registerForBlockEntity(BiFunction<T, Direction, @Nullable EnergyStorage> provider, BlockEntityType<T> blockEntityType) {
        if (blockEntityProviders.containsKey(blockEntityType)) {
            ESL.LOGGER.trace("Duplicate energy storage registration for " + blockEntityType);
            return;
        }

        blockEntityProviders.put(blockEntityType, (BiFunction<BlockEntity, Direction, EnergyStorage>) provider);
        team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            EnergyStorage storage = provider.apply(blockEntity, direction);
            // PORT-NOTE(26.1): upstream wrapped a null storage too; TR Energy expects null for "no storage here".
            return storage == null ? null : E2FEnergyStorageAdapter.getOrCreate(storage);
        }, blockEntityType);
    }

    public boolean isBlockEntityRegisteredInESL(BlockEntityType<?> blockEntityType) {
        return blockEntityProviders.containsKey(blockEntityType);
    }

    public EnergyStorage find(Level level, BlockPos pos, @Nullable Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be != null) {
            var provider = blockEntityProviders.get(be.getType());

            if (provider != null) {
                EnergyStorage storage = provider.apply(be, direction);

                if (storage != null) {
                    return storage;
                }
            }
        }

        team.reborn.energy.api.EnergyStorage storage = team.reborn.energy.api.EnergyStorage.SIDED.find(level, pos, direction);
        return storage == null ? null : F2EEnergyStorageAdapter.getOrCreate(storage);
    }
}
