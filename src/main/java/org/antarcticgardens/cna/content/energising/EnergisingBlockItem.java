package org.antarcticgardens.cna.content.energising;

import com.zurrtum.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.world.level.block.Block;


public class EnergisingBlockItem extends AssemblyOperatorBlockItem {
    public EnergisingBlockItem(Block block, Properties builder) {
        super(block, builder);
    }

    // PORT-NOTE(26.1): the NeoForge custom item renderer (block model + a static shaft overlay) is gone; the item uses
    // its plain item definition for now (TODO: composite item model, see PORTING-26.1.md).
}
