package org.antarcticgardens.cna.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * PORT-NOTE(26.1): Block.appendHoverText is gone. Blocks that had one implement this; the client ItemTooltipCallback in
 * CreateNewAgeClient adds the lines to their block items.
 */
public interface HoverTextBlock {
    void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag);
}
