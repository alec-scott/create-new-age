/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.fabric.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.antarcticgardens.cna.port.esl.energy.EnergyStorage;
import org.antarcticgardens.cna.port.esl.fabric.transaction.FabricTransaction;

import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("UnstableApiUsage")
public class E2FEnergyStorageAdapter implements team.reborn.energy.api.EnergyStorage {
    private static final Map<EnergyStorage, E2FEnergyStorageAdapter> cache = new WeakHashMap<>();
    
    private final EnergyStorage handle;
    
    E2FEnergyStorageAdapter(EnergyStorage handle) {
        this.handle = handle;
    }
    
    public static E2FEnergyStorageAdapter getOrCreate(EnergyStorage handle) {
        E2FEnergyStorageAdapter adapter = cache.get(handle);

        if (adapter != null) {
            return adapter;
        }

        adapter = new E2FEnergyStorageAdapter(handle);
        cache.put(handle, adapter);
        return adapter;
    }
    
    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return handle.insert(maxAmount, new FabricTransaction((Transaction) transaction));
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return handle.extract(maxAmount, new FabricTransaction((Transaction) transaction));
    }

    @Override
    public long getAmount() {
        return handle.getStoredEnergy();
    }

    @Override
    public long getCapacity() {
        return handle.getCapacity();
    }

    @Override
    public boolean supportsInsertion() {
        return handle.supportsInsertion();
    }

    @Override
    public boolean supportsExtraction() {
        return handle.supportsExtraction();
    }
}
