package org.antarcticgardens.cna;

import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import org.antarcticgardens.cna.content.ponders.CNAPonders;
import org.antarcticgardens.cna.port.create.foundation.data.CreateRegistrate;

/**
 * PORT-NOTE(26.1): was CreateNewAgeClient + CreateNewAgeClientNeoForge. The NeoForge config screen extension point
 * (catnip BaseConfigScreen) has no Fabric equivalent without Mod Menu and is dropped for now.
 */
public class CreateNewAgeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CNAPartialModels.load();
        CNASpriteShifts.load();
        CreateRegistrate.registerBlockModels();
        org.antarcticgardens.cna.client.CNABlockEntityBehaviours.register();
        PonderIndex.addPlugin(new CNAPonders());

        // Was Block.appendHoverText (removed in 26.1): block item tooltips, right under the item name
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                    && blockItem.getBlock() instanceof org.antarcticgardens.cna.util.HoverTextBlock block) {
                java.util.List<net.minecraft.network.chat.Component> extra = new java.util.ArrayList<>();
                block.appendHoverText(stack, context, extra, flag);
                lines.addAll(Math.min(1, lines.size()), extra);
            }
        });
    }
}
