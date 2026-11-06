/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.energy;

import org.antarcticgardens.cna.port.esl.transaction.SnapshotParticipant;
import org.antarcticgardens.cna.port.esl.transaction.TransactionContext;

public class SimpleEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    private long capacity;
    private long stored = 0;
    private boolean supportsInsertion = true;
    private boolean supportsExtraction = true;
    private long maxExtract = Long.MAX_VALUE;
    private long maxInsert = Long.MAX_VALUE;
    
    private Runnable finalCommitCallback = () -> {};
    
    public SimpleEnergyStorage(long capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("SimpleEnergyStorage capacity can't be negative");
        }
        
        this.capacity = capacity;
    }
    
    public SimpleEnergyStorage onFinalCommit(Runnable callback) {
        finalCommitCallback = callback;
        return this;
    }
    
    public SimpleEnergyStorage setCapacity(long capacity) {
        this.capacity = capacity;
        stored = Math.min(stored, capacity);
        
        return this;
    }
    
    public SimpleEnergyStorage setSupportsInsertion(boolean supportsInsertion) {
        this.supportsInsertion = supportsInsertion;
        return this;
    }
    
    public SimpleEnergyStorage setSupportsExtraction(boolean supportsExtraction) {
        this.supportsExtraction = supportsExtraction;
        return this;
    }

    public SimpleEnergyStorage setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
        return this;
    }

    public SimpleEnergyStorage setMaxInsert(long maxInsert) {
        this.maxInsert = maxInsert;
        return this;
    }

    public SimpleEnergyStorage setStoredEnergy(long amount) {
        stored = Math.max(0, Math.min(amount, capacity));
        return this;
    }

    @Override
    protected void onFinalCommit() {
        finalCommitCallback.run();
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getStoredEnergy() {
        return stored;
    }
    
    @Override
    public Long createSnapshot() {
        return stored;
    }

    @Override
    public void restoreSnapshot(Long snapshot) {
        stored = snapshot;
    }

    @Override
    public long insert(long amount, TransactionContext context) {
        if (!supportsInsertion) {
            return 0;
        }
        
        updateSnapshots(context);
        
        long inserted = Math.min(Math.min(amount, maxInsert), capacity - stored);
        stored += inserted;
        
        return inserted;
    }

    @Override
    public long extract(long amount, TransactionContext context) {
        if (!supportsExtraction) {
            return 0;
        }
        
        updateSnapshots(context);
        
        long extracted = Math.min(Math.min(amount, maxExtract), stored);
        stored -= extracted;
        
        return extracted;
    }
    
    public long internalInsert(long amount, boolean simulated) {
        long inserted = Math.min(amount, capacity - stored);
        
        if (simulated) {
            return inserted;
        }
        
        stored += inserted;
        return inserted;
    }

    public long internalExtract(long amount, boolean simulated) {
        long extracted = Math.min(amount, stored);
        
        if (simulated) {
            return extracted;
        }
        
        stored -= extracted;
        return extracted;
    }

    @Override
    public boolean supportsExtraction() {
        return supportsExtraction;
    }

    @Override
    public boolean supportsInsertion() {
        return supportsInsertion;
    }
}
