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
        PonderIndex.addPlugin(new CNAPonders());
    }
}
