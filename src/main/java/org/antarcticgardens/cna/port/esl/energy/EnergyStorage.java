/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.port.esl.ESL;
import org.antarcticgardens.cna.port.esl.transaction.TransactionContext;
import org.antarcticgardens.cna.port.esl.util.ItemHolder;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface EnergyStorage {
    long insert(long amount, TransactionContext context);
    long extract(long amount, TransactionContext context);
    long getStoredEnergy();
    long getCapacity();

    default boolean supportsExtraction() {
        return true;
    }

    default boolean supportsInsertion() {
        return true;
    }

    static EnergyStorage findForBlock(Level level, BlockPos pos, @Nullable Direction direction) {
        return ESL.getInstance().getBlockEnergyStorageManager().find(level, pos, direction);
    }

    static <T extends BlockEntity> void registerForBlockEntity(BiFunction<T, Direction, @Nullable EnergyStorage> provider, BlockEntityType<T> blockEntityType) {
        ESL.getInstance().getBlockEnergyStorageManager().registerForBlockEntity(provider, blockEntityType);
    }

    static EnergyStorage findForItem(ItemHolder holder) {
        return ESL.getInstance().getItemEnergyStorageManager().find(holder);
    }

    static void registerForItem(Function<ItemHolder, EnergyStorage> provider, ItemLike item) {
        ESL.getInstance().getItemEnergyStorageManager().register(provider, item);
    }
}
