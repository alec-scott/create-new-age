package org.antarcticgardens.cna;

import com.zurrtum.create.api.boiler.BoilerHeater;
import com.zurrtum.create.catnip.placement.PlacementHelpers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.antarcticgardens.cna.compat.computercraft.CNAComputerCraftProxy;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.magnet.MagnetPlacementHelper;
import org.antarcticgardens.cna.content.electricity.network.NetworkTicker;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.antarcticgardens.cna.port.create.CreateTransforms;
import org.antarcticgardens.cna.port.create.foundation.data.CreateRegistrate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * PORT-NOTE(26.1): was the abstract common entrypoint plus CreateNewAgeNeoForge/NeoForgePlatform/NeoForgeRegistrar
 * (Architectury split). Collapsed into one Fabric {@link ModInitializer}:
 * <ul>
 *     <li>energy capabilities: ESL now registers with TR Energy's {@code EnergyStorage.SIDED} directly
 *     ({@code port/esl}), so the NeoForge {@code RegisterCapabilitiesEvent} handler is gone;</li>
 *     <li>{@code LevelTickEvent.Pre} → {@code ServerTickEvents.START_LEVEL_TICK} (networks only exist server side);</li>
 *     <li>biome modifiers (NeoForge datagen JSON) → {@link BiomeModifications};</li>
 *     <li>the "monkey edition" data pack ({@code AddPackFindersEvent}) → {@link ResourceLoader#registerBuiltinPack}.</li>
 * </ul>
 */
public class CreateNewAge implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "create_new_age";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(MOD_ID, "tab"));

    public static final ResourceKey<PlacedFeature> THORIUM_ORE = ResourceKey.create(Registries.PLACED_FEATURE, asResource("thorium_ore"));
    public static final ResourceKey<PlacedFeature> MAGNETITE_BLOCK = ResourceKey.create(Registries.PLACED_FEATURE, asResource("magnetite_block"));

    public static boolean CC_ACTIVE = false;

    private static CreateNewAge instance;

    private int magnetPlacementHelperId;

    public CreateNewAge() {
        instance = this;
    }

    @Override
    public void onInitialize() {
        CC_ACTIVE = FabricLoader.getInstance().isModLoaded("computercraft");

        CNAConfig.load();

        CNABlocks.load();
        CNABlockEntityTypes.load();
        CNAItems.load();
        CNAEffects.load();
        CNASounds.load();
        CNATags.load();
        CNADataComponents.load();
        CNARecipeTypes.register();
        registerCreativeTab();

        REGISTRATE.register();
        CreateTransforms.apply();

        magnetPlacementHelperId = PlacementHelpers.register(new MagnetPlacementHelper());

        // Energy (ESL -> TR Energy EnergyStorage.SIDED); was a NeoForge RegisterCapabilitiesEvent handler
        org.antarcticgardens.cna.content.motor.MotorBlockEntity.registerEnergyStorage();
        org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlockEntity.registerEnergyStorage();
        org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity.registerEnergyStorage();
        org.antarcticgardens.cna.content.energising.EnergiserBlockEntity.registerEnergyStorage();
        org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity.registerEnergyStorage();
        org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity.registerItemStorage();
        CNAComputerCraftProxy.register();

        ServerTickEvents.START_LEVEL_TICK.register(NetworkTicker::tickWorld);

        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_OVERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, THORIUM_ORE);
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_OVERWORLD), GenerationStep.Decoration.UNDERGROUND_ORES, MAGNETITE_BLOCK);

        ModContainer mod = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
        ResourceLoader.registerBuiltinPack(asResource("create_new_age_monkey_edition"), mod,
                Component.translatable("create_new_age.monkey_edition"), PackActivationType.NORMAL);

        // Was FMLCommonSetupEvent
        BoilerHeater.REGISTRY.register(CNABlocks.HEATER.get(), (level, pos, state) -> state.getValue(HeaterBlock.STRENGTH).ordinal() - 1);
        for (Runnable run : CNABlocks.doLater) {
            run.run();
        }
    }

    // PORT-NOTE(26.1): Fabric tab registration (was a NeoForge DeferredRegister); the port Registrate fills it with
    // every entry registered under defaultCreativeTab(CREATIVE_TAB_KEY).
    private static void registerCreativeTab() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_KEY, FabricCreativeModeTab.builder()
                .title(Component.translatable("tab." + MOD_ID + ".tab"))
                .icon(() -> CNABlocks.GENERATOR_COIL.asStack())
                .build());
    }

    public int getMagnetPlacementHelperId() {
        return magnetPlacementHelperId;
    }

    public static CreateNewAge getInstance() {
        return instance;
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
