/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.fabric.mixin;

import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnstableApiUsage")
@Mixin(SnapshotParticipant.class)
public interface SnapshotParticipantInvoker<T> {
    @Invoker(value = "createSnapshot")
    T invokeCreateSnapshot();
    
    @Invoker(value = "readSnapshot")
    void invokeReadSnapshot(T snapshot);
}
