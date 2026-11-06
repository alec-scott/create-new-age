package org.antarcticgardens.cna.util;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

/**
 * PORT-NOTE(26.1): Create Fly attaches client behaviours (goggle tooltips, value boxes) in SmartBlockEntity.initialize,
 * which runs from SmartBlockEntity.tick. Blocks with their own tickers wrap them with this so that tick still runs.
 */
public final class SmartTicker {
    private SmartTicker() {
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> wrap(BlockEntityTicker<T> inner) {
        return (level, pos, state, be) -> {
            if (be instanceof SmartBlockEntity smart)
                smart.tick();
            if (inner != null)
                inner.tick(level, pos, state, be);
        };
    }
}
