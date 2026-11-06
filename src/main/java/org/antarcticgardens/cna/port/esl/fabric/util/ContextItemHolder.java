/*
 * Relocated from ESL (Energy Storage Library), https://gitlab.com/antarcticgardens/esl (branch 1.21.1).
 * Copyright (c) 2024 Antarctic Gardens. BSD 3-Clause licence, see NOTICE in org/antarcticgardens/cna/port/esl.
 * PORT-NOTE(26.1): Mojang names, Fabric only (common/platform split collapsed), Team Reborn Energy 5.0.0.
 */
package org.antarcticgardens.cna.port.esl.fabric.util;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.cna.port.esl.fabric.transaction.FabricTransaction;
import org.antarcticgardens.cna.port.esl.transaction.TransactionUtil;
import org.antarcticgardens.cna.port.esl.util.ItemHolder;
import org.antarcticgardens.cna.port.esl.util.Mth;

@SuppressWarnings("UnstableApiUsage")
public class ContextItemHolder implements ItemHolder {
    private final ContainerItemContext context;

    public ContextItemHolder(ContainerItemContext context) {
        this.context = context;
    }

    @Override
    public Item getItem() {
        return context.getItemVariant().getItem();
    }

    @Override
    public long getAmount() {
        return context.getAmount();
    }

    @Override
    public DataComponentPatch getComponents() {
        return context.getItemVariant().getComponentsPatch();
    }

    @Override
    public void setItem(Item item) {
        setVariant(ItemVariant.of(item, getComponents()));
    }

    @Override
    public void setAmount(long amount) {
        try (FabricTransaction t = (FabricTransaction) TransactionUtil.openOuterOrNested()) {
            if (context.getAmount() > amount) {
                context.extract(context.getItemVariant(), context.getAmount() - amount, t.getHandle());
            } else if (context.getAmount() < amount) {
                context.insert(context.getItemVariant(), amount - context.getAmount(), t.getHandle());
            }
        }
    }

    @Override
    public void setComponents(DataComponentPatch componentChanges) {
        setVariant(ItemVariant.of(getItem(), componentChanges));
    }
    
    public void setVariant(ItemVariant variant) {
        try (FabricTransaction t = (FabricTransaction) TransactionUtil.openOuterOrNested()) {
            context.exchange(variant, context.getAmount(), t.getHandle());
            t.commit();
        }
    }

    @Override
    public void fromItemStack(ItemStack stack) {
        setVariant(ItemVariant.of(stack));
        setAmount(stack.getCount());
    }

    @Override
    public ItemStack toItemStack() {
        return context.getItemVariant().toStack(Mth.longToIntSaturated(getAmount()));
    }
}
