package org.antarcticgardens.cna.config;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.antarcticgardens.cna.CreateNewAge;

public class CNAConfig {
    private static final CNAConfig INSTANCE = new CNAConfig();

    private final ClientConfig client;
    private final ServerConfig server;

    private CNAConfig() {
        var client = new ModConfigSpec.Builder().configure(ClientConfig::new);
        this.client = client.getLeft();
        ConfigRegistry.INSTANCE.register(CreateNewAge.MOD_ID, ModConfig.Type.CLIENT, client.getRight());

        var common = new ModConfigSpec.Builder().configure(ServerConfig::new);
        this.server = common.getLeft();
        ConfigRegistry.INSTANCE.register(CreateNewAge.MOD_ID, ModConfig.Type.SERVER, common.getRight());
    }

    public static ClientConfig getClient() {
        return INSTANCE.client;
    }

    public static ServerConfig getServer() {
        return INSTANCE.server;
    }
    
    public static void load() {  }
}
