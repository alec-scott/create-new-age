/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.transaction;

public interface TransactionContext {
    Transaction openNested();
    int getNestingDepth();
    Transaction getOpenTransaction(int depth);
    void addCloseCallback(CloseCallback callback);
    void addOuterCloseCallback(OuterCloseCallback callback);

    @FunctionalInterface
    interface CloseCallback {
        void onClose(TransactionContext context, TransactionResult result);
    }
    
    @FunctionalInterface
    interface OuterCloseCallback {
        void onOuterClose(TransactionResult result);
    }
}
