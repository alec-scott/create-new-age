package org.antarcticgardens.cna.client;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.CNABlockEntityTypes;

import java.util.function.Function;

/**
 * PORT-NOTE(26.1): Create Fly keeps a block entity's client-only parts (goggle tooltips, value boxes) in client
 * behaviours registered per type, like its own AllBlockEntityBehaviours (and CC&A's CABlockEntityBehaviours).
 */
public final class CNABlockEntityBehaviours {
    private CNABlockEntityBehaviours() {
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <T extends SmartBlockEntity> void add(BlockEntityType<T> type, Function<T, BlockEntityBehaviour<?>>... factories) {
        for (Function<T, BlockEntityBehaviour<?>> factory : factories)
            BlockEntityBehaviour.CLIENT_REGISTRY.add(type, (Function<SmartBlockEntity, BlockEntityBehaviour<?>>) factory);
    }

    public static void register() {
        // Kinetic block entities: Create Fly's stress/speed goggle lines, then New Age's own
        add(CNABlockEntityTypes.BASIC_MOTOR.get(), CNAKineticTooltipBehaviour.Generating::new, CNAMotorValueBoxes.Motor::new);
        add(CNABlockEntityTypes.ADVANCED_MOTOR.get(), CNAKineticTooltipBehaviour.Generating::new, CNAMotorValueBoxes.Motor::new);
        add(CNABlockEntityTypes.REINFORCED_MOTOR.get(), CNAKineticTooltipBehaviour.Generating::new, CNAMotorValueBoxes.Motor::new);
        add(CNABlockEntityTypes.STIRLING_ENGINE.get(), CNAKineticTooltipBehaviour.Generating::new);
        add(CNABlockEntityTypes.ENERGISER.get(), CNAKineticTooltipBehaviour.Consuming::new);
        add(CNABlockEntityTypes.GENERATOR_COIL.get(), CNAKineticTooltipBehaviour.Consuming::new);
        add(CNABlockEntityTypes.CARBON_BRUSHES.get(), CNAKineticTooltipBehaviour.Consuming::new);

        add(CNABlockEntityTypes.BASIC_MOTOR_EXTENSION.get(), CNAMotorValueBoxes.Extension::new);
        add(CNABlockEntityTypes.ADVANCED_MOTOR_EXTENSION.get(), CNAMotorValueBoxes.Extension::new);

        add(CNABlockEntityTypes.ELECTRICAL_CONNECTOR.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.STREET_LIGHT.get(), CNATooltipBehaviour::new, CNAMotorValueBoxes.StreetLight::new);
        add(CNABlockEntityTypes.HEAT_PIPE.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.ENCASED_HEAT_PIPE.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.HEAT_PUMP.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.HEATER.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.BASIC_SOLAR_HEATING_PLATE.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.ADVANCED_SOLAR_HEATING_PLATE.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.REACTOR_ROD.get(), CNATooltipBehaviour::new);
        add(CNABlockEntityTypes.REACTOR_HEAT_VENT.get(), CNATooltipBehaviour::new);
    }
}
