/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.util;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemStackHolder implements ItemHolder {
    private ItemStack stack;
    
    public ItemStackHolder(ItemStack stack) {
        this.stack = stack;
    }
    
    @Override
    public Item getItem() {
        return stack.getItem();
    }

    @Override
    public long getAmount() {
        return stack.getCount();
    }

    @Override
    public DataComponentPatch getComponents() {
        return stack.getComponentsPatch();
    }

    @Override
    public void setItem(Item item) {
        stack = new ItemStack(item, Mth.longToIntSaturated(getAmount()));
        stack.applyComponents(getComponents());
    }

    @Override
    public void setAmount(long amount) {
        stack.setCount(Mth.longToIntSaturated(amount));
    }

    @Override
    public void setComponents(DataComponentPatch componentChanges) {
        stack.applyComponents(componentChanges);
    }

    @Override
    public void fromItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack toItemStack() {
        return stack;
    }
}
