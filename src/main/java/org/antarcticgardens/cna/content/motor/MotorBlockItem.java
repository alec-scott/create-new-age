package org.antarcticgardens.cna.content.motor;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;


public class MotorBlockItem extends BlockItem {
    public MotorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    // PORT-NOTE(26.1): the NeoForge custom item renderer (block model + a static shaft overlay) is gone; the item uses
    // its plain item definition for now (TODO: composite item model, see PORTING-26.1.md).
}
