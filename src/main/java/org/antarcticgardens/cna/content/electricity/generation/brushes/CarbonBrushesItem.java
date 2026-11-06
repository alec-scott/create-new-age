package org.antarcticgardens.cna.content.electricity.generation.brushes;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;


public class CarbonBrushesItem extends BlockItem {
    public CarbonBrushesItem(Block block, Properties properties) {
        super(block, properties);
    }

    // PORT-NOTE(26.1): the NeoForge custom item renderer (block model + a static shaft overlay) is gone; the item uses
    // its plain item definition for now (TODO: composite item model, see PORTING-26.1.md).
}
