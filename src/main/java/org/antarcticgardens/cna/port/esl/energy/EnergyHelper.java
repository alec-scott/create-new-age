/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.antarcticgardens.cna.port.esl.transaction.Transaction;
import org.antarcticgardens.cna.port.esl.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;

public class EnergyHelper {
    public static long moveEnergy(EnergyStorage from, EnergyStorage to, long amount, TransactionContext txn) {
        try (Transaction test = txn.openNested()) {
            long finalAmount = to.insert(amount, test);
            finalAmount = from.extract(finalAmount, test);
            test.abort();
            to.insert(finalAmount, txn);
            from.extract(finalAmount, txn);
            return finalAmount;
        }
    }

    public static long insertToSurrounding(EnergyStorage storage, BlockPos pos, Level level, long maxAmount, TransactionContext txn) {
        List<EnergyStorage> directionStorages = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            EnergyStorage directionStorage = EnergyStorage.findForBlock(level, pos.relative(dir), dir.getOpposite());
            if (directionStorage != null) {
                directionStorages.add(directionStorage);
            }
        }

        int storagesLeft = directionStorages.size();
        long inserted = 0;
        for (EnergyStorage directionStorage : directionStorages) {
            inserted += moveEnergy(storage, directionStorage, maxAmount / storagesLeft--, txn);
        }

        return inserted;
    }
}
