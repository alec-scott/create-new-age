/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.transaction;

public class TransactionUtil {
    public static Transaction openOuterOrNested() {
        if (TransactionStack.get().getDepth() == -1) {
            return TransactionStack.get().openOuter();
        } else {
            return TransactionStack.get().getCurrent().openNested();
        }
    }
}
