/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.transaction;

import java.util.HashMap;
import java.util.Map;

public abstract class SnapshotParticipant<T> {
    private final Map<Integer, T> snapshots = new HashMap<>();
    
    public abstract T createSnapshot();
    public abstract void restoreSnapshot(T snapshot);
    
    protected void onFinalCommit() {
        
    }
    
    protected void updateSnapshots(TransactionContext context) {
        if (!snapshots.containsKey(context.getNestingDepth())) {
            context.addCloseCallback(this::onClose);
            snapshots.put(context.getNestingDepth(), createSnapshot());
        }
    }
    
    private void onClose(TransactionContext context, TransactionResult result) {
        T snapshot = snapshots.remove(context.getNestingDepth());
        
        if (result.equals(TransactionResult.ABORTED)) {
            restoreSnapshot(snapshot);
        } else if (context.getNestingDepth() > 0) {
            int parent = context.getNestingDepth() - 1;

            if (!snapshots.containsKey(parent)) {
                snapshots.put(parent, snapshot);
                context.getOpenTransaction(parent).addCloseCallback(this::onClose);
            }
        } else {
            context.addOuterCloseCallback(r -> onFinalCommit());
        }
    }
}
