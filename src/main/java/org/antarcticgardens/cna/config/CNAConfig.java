package org.antarcticgardens.cna.config;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.antarcticgardens.cna.CreateNewAge;

import java.util.function.Function;

public class CNAConfig {
    private static final CNAConfig INSTANCE = new CNAConfig();

    private final ClientConfig client;
    private final ServerConfig server;
    private final ModConfigSpec serverSpec;

    private CNAConfig() {
        var client = new ModConfigSpec.Builder().configure(ClientConfig::new);
        this.client = client.getLeft();
        ConfigRegistry.INSTANCE.register(CreateNewAge.MOD_ID, ModConfig.Type.CLIENT, client.getRight());

        var common = new ModConfigSpec.Builder().configure(ServerConfig::new);
        this.server = common.getLeft();
        this.serverSpec = common.getRight();
        ConfigRegistry.INSTANCE.register(CreateNewAge.MOD_ID, ModConfig.Type.SERVER, common.getRight());
    }

    public static ClientConfig getClient() {
        return INSTANCE.client;
    }

    public static ServerConfig getServer() {
        return INSTANCE.server;
    }

    /**
     * PORT-NOTE(26.1): reads a server config value, falling back to its default while the server config is not
     * loaded. A client only has it after the server syncs it; reading it unloaded (wire rendering, tooltips) threw
     * "Cannot get config value before config is loaded" and crashed the client.
     */
    public static <T> T server(Function<ServerConfig, ModConfigSpec.ConfigValue<T>> value) {
        ModConfigSpec.ConfigValue<T> configValue = value.apply(INSTANCE.server);
        return INSTANCE.serverSpec.isLoaded() ? configValue.get() : configValue.getDefault();
    }
    
    public static void load() {  }
}
