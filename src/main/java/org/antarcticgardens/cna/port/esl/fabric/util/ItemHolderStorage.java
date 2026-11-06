/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.fabric.util;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.cna.port.esl.util.ItemHolder;

@SuppressWarnings("UnstableApiUsage")
public class ItemHolderStorage extends SingleStackStorage {
    private final ItemHolder holder;
    
    public ItemHolderStorage(ItemHolder holder) {
        this.holder = holder;
    }
    
    @Override
    protected ItemStack getStack() {
        return holder.toItemStack();
    }

    @Override
    protected void setStack(ItemStack stack) {
        holder.fromItemStack(stack);
    }
}
