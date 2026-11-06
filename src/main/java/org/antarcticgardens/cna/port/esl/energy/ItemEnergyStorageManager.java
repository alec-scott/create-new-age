/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.energy;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.level.ItemLike;
import org.antarcticgardens.cna.port.esl.ESL;
import org.antarcticgardens.cna.port.esl.fabric.energy.E2FEnergyStorageAdapter;
import org.antarcticgardens.cna.port.esl.fabric.energy.F2EEnergyStorageAdapter;
import org.antarcticgardens.cna.port.esl.fabric.util.ContextItemHolder;
import org.antarcticgardens.cna.port.esl.fabric.util.ItemHolderStorage;
import org.antarcticgardens.cna.port.esl.util.ItemHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** PORT-NOTE(26.1): FabricItemEnergyStorageManager merged in (registers with TR Energy's {@code EnergyStorage.ITEM}). */
public class ItemEnergyStorageManager {
    private final Map<ItemLike, Function<ItemHolder, EnergyStorage>> providers = new HashMap<>();

    public void register(Function<ItemHolder, EnergyStorage> provider, ItemLike item) {
        if (providers.containsKey(item)) {
            ESL.LOGGER.trace("Duplicate energy storage registration for " + item);
            return;
        }

        providers.put(item, provider);
        team.reborn.energy.api.EnergyStorage.ITEM.registerForItems((itemStack, context) -> {
            EnergyStorage storage = provider.apply(new ContextItemHolder(context));
            return storage == null ? null : E2FEnergyStorageAdapter.getOrCreate(storage);
        }, item);
    }

    public boolean isItemRegisteredInESL(ItemLike item) {
        return providers.containsKey(item);
    }

    public EnergyStorage find(ItemHolder holder) {
        var provider = providers.get(holder.getItem());

        if (provider != null) {
            EnergyStorage storage = provider.apply(holder);

            if (storage != null) {
                return storage;
            }
        }

        team.reborn.energy.api.EnergyStorage storage = team.reborn.energy.api.EnergyStorage.ITEM.find(holder.toItemStack(),
                ContainerItemContext.ofSingleSlot(new ItemHolderStorage(holder)));
        return storage == null ? null : F2EEnergyStorageAdapter.getOrCreate(storage);
    }
}
