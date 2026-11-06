package org.antarcticgardens.cna.client;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.api.goggles.IHaveHoveringInformation;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * PORT-NOTE(26.1): Create Fly's goggle overlay reads a client TooltipBehaviour, not the block entity. This forwards to
 * New Age block entities that still implement IHaveGoggleInformation / IHaveHoveringInformation themselves.
 */
public class CNATooltipBehaviour<T extends SmartBlockEntity> extends TooltipBehaviour<T> implements IHaveGoggleInformation, IHaveHoveringInformation {
    public CNATooltipBehaviour(T be) {
        super(be);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return blockEntity instanceof IHaveGoggleInformation info && info.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return blockEntity instanceof IHaveHoveringInformation info && info.addToTooltip(tooltip, isPlayerSneaking);
    }
}
