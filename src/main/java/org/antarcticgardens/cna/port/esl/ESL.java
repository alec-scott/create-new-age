/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl;

import org.antarcticgardens.cna.port.esl.energy.BlockEnergyStorageManager;
import org.antarcticgardens.cna.port.esl.energy.ItemEnergyStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** PORT-NOTE(26.1): was abstract with ESLFabric/ESLNeoForge subclasses; now a Fabric-only singleton. */
public final class ESL {
    public static final Logger LOGGER = LogManager.getLogger("create_new_age/esl");

    private static final ESL INSTANCE = new ESL();

    private final BlockEnergyStorageManager blockEnergyStorageManager = new BlockEnergyStorageManager();
    private final ItemEnergyStorageManager itemEnergyStorageManager = new ItemEnergyStorageManager();

    private ESL() {
    }

    public BlockEnergyStorageManager getBlockEnergyStorageManager() {
        return blockEnergyStorageManager;
    }

    public ItemEnergyStorageManager getItemEnergyStorageManager() {
        return itemEnergyStorageManager;
    }

    public static ESL getInstance() {
        return INSTANCE;
    }
}
