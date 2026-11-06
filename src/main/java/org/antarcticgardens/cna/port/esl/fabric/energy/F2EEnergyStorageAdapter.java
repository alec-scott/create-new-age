/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.fabric.energy;

import org.antarcticgardens.cna.port.esl.energy.EnergyStorage;
import org.antarcticgardens.cna.port.esl.fabric.mixin.SnapshotParticipantInvoker;
import org.antarcticgardens.cna.port.esl.fabric.transaction.FabricTransaction;
import org.antarcticgardens.cna.port.esl.transaction.SnapshotParticipant;

import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class F2EEnergyStorageAdapter extends SnapshotParticipant<Object> implements EnergyStorage {
    private static final Map<team.reborn.energy.api.EnergyStorage, F2EEnergyStorageAdapter> cache = new WeakHashMap<>();
    
    private final team.reborn.energy.api.EnergyStorage handle;
    private final SnapshotParticipantInvoker<Object> snapshotParticipantInvoker;
    
    F2EEnergyStorageAdapter(team.reborn.energy.api.EnergyStorage handle) {
        this.handle = handle;
        
        if (handle instanceof SnapshotParticipantInvoker<?> inv) {
            snapshotParticipantInvoker = (SnapshotParticipantInvoker<Object>) inv;
        } else {
            snapshotParticipantInvoker = null;
        }
    }
    
    public static F2EEnergyStorageAdapter getOrCreate(team.reborn.energy.api.EnergyStorage handle) {
        F2EEnergyStorageAdapter adapter = cache.get(handle);

        if (adapter != null) {
            return adapter;
        }

        adapter = new F2EEnergyStorageAdapter(handle);
        cache.put(handle, adapter);
        return adapter;
    }
    
    @Override
    public long insert(long amount, org.antarcticgardens.cna.port.esl.transaction.TransactionContext transaction) {
        return handle.insert(amount, ((FabricTransaction) transaction).getHandle()); 
    }

    @Override
    public long extract(long amount, org.antarcticgardens.cna.port.esl.transaction.TransactionContext transaction) {
        return handle.extract(amount, ((FabricTransaction) transaction).getHandle());
    }

    @Override
    public long getStoredEnergy() {
        return handle.getAmount();
    }

    @Override
    public long getCapacity() {
        return handle.getCapacity();
    }

    @Override
    public boolean supportsExtraction() {
        return handle.supportsExtraction();
    }

    @Override
    public boolean supportsInsertion() {
        return handle.supportsInsertion();
    }

    @Override
    public Object createSnapshot() {
        if (snapshotParticipantInvoker != null) {
            return snapshotParticipantInvoker.invokeCreateSnapshot();
        }
        
        return null;
    }

    @Override
    public void restoreSnapshot(Object snapshot) {
        if (snapshotParticipantInvoker != null) {
            snapshotParticipantInvoker.invokeReadSnapshot(snapshot);
        }
    }
}
