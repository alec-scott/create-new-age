package org.antarcticgardens.cna.compat.computercraft;

import org.antarcticgardens.cna.CreateNewAge;

/**
 * PORT-NOTE(26.1): upstream gave each block entity a CNAComputerBehaviour (or Create's fallback) and exposed it through
 * NeoForge's PeripheralCapability. Create Fly attaches its ComputerBehaviour per block entity type and caches the
 * peripheral in it (AllComputerPeripherals), so this only forwards to {@link CNAPeripherals} when CC:Tweaked is loaded;
 * no CC classes load otherwise.
 */
public class CNAComputerCraftProxy {
    public static void register() {
        if (CreateNewAge.CC_ACTIVE)
            CNAPeripherals.register();
    }
}
