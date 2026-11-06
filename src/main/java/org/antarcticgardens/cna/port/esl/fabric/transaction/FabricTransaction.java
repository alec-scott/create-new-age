/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.fabric.transaction;

import org.antarcticgardens.cna.port.esl.transaction.Transaction;
import org.antarcticgardens.cna.port.esl.transaction.TransactionResult;

@SuppressWarnings("UnstableApiUsage")
public class FabricTransaction implements Transaction {
    private final net.fabricmc.fabric.api.transfer.v1.transaction.Transaction handle;
    private boolean open = true;
    
    public FabricTransaction(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction handle) {
        this.handle = handle;
    }

    @Override
    public void commit() {
        handle.commit();
        open = false;
    }

    @Override
    public void abort() {
        handle.abort();
        open = false;
    }

    @Override
    public void close() {
        handle.close();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public Transaction openNested() {
        return new FabricTransaction(handle.openNested());
    }

    @Override
    public int getNestingDepth() {
        return handle.nestingDepth();
    }

    @Override
    public void addCloseCallback(CloseCallback callback) {
        handle.addCloseCallback((transaction, result) -> callback.onClose(this, TransactionResult.valueOf(result.name())));
    }

    @Override
    public void addOuterCloseCallback(OuterCloseCallback callback) {
        handle.addOuterCloseCallback(result -> callback.onOuterClose(TransactionResult.valueOf(result.name())));
    }

    @Override
    public Transaction getOpenTransaction(int depth) {
        return new FabricTransaction(handle.getOpenTransaction(depth));
    }
    
    public net.fabricmc.fabric.api.transfer.v1.transaction.Transaction getHandle() {
        return handle;
    }
}
