/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.util;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ItemHolder {
    Item getItem();
    long getAmount();
    DataComponentPatch getComponents();
    
    void setItem(Item item);
    void setAmount(long amount);
    void setComponents(DataComponentPatch componentChanges);
    
    void fromItemStack(ItemStack stack);
    ItemStack toItemStack();
}
