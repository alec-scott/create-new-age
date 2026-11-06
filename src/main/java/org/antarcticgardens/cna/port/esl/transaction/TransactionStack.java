/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.transaction;

import org.antarcticgardens.cna.port.esl.fabric.transaction.FabricTransaction;

/** PORT-NOTE(26.1): FabricTransactionStack merged in; ESL transactions are Fabric transfer API transactions. */
public class TransactionStack {
    private static final ThreadLocal<TransactionStack> LOCAL = ThreadLocal.withInitial(TransactionStack::new);

    public Transaction openOuter() {
        return new FabricTransaction(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter());
    }

    public Transaction getCurrent() {
        net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext current =
                net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.getCurrentUnsafe();

        if (!(current instanceof net.fabricmc.fabric.api.transfer.v1.transaction.Transaction txn)) {
            return null;
        }

        return new FabricTransaction(txn);
    }

    public int getDepth() {
        Transaction current = getCurrent();

        if (current == null) {
            return -1;
        }

        return current.getNestingDepth();
    }

    public static TransactionStack get() {
        return LOCAL.get();
    }
}
