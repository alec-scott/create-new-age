package org.antarcticgardens.cna.compat.computercraft;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.compat.computercraft.AbstractComputerBehaviour;
import com.zurrtum.create.compat.computercraft.implementation.ComputerBehaviour;
import com.zurrtum.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.compat.computercraft.peripherals.CarbonBrushesBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.EnergiserBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.MotorBlockEntityPeripheral;

import java.util.function.Function;

/**
 * PORT-NOTE(26.1): CC:Tweaked's Fabric {@link PeripheralLookup}, the same pattern as Create Fly's AllComputerPeripherals:
 * the behaviour is added per type and the peripheral is cached in it, so a re-query returns the same peripheral (see
 * CC&A's 2026-11-06 log: a new peripheral per query makes CC detach and drop pending mainThread calls). Only loaded
 * when CC:Tweaked is present.
 */
public class CNAPeripherals {
    public static void register() {
        registerPeripheral(CNABlockEntityTypes.ENERGISER.get(), EnergiserBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.BASIC_MOTOR.get(), MotorBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.ADVANCED_MOTOR.get(), MotorBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.REINFORCED_MOTOR.get(), MotorBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.CARBON_BRUSHES.get(), CarbonBrushesBlockEntityPeripheral::new);
    }

    private static <T extends SmartBlockEntity> void registerPeripheral(BlockEntityType<T> type, Function<T, SyncedPeripheral<T>> factory) {
        BlockEntityBehaviour.add(type, ComputerBehaviour::new);
        PeripheralLookup.get().registerForBlockEntity((blockEntity, direction) -> {
            if (blockEntity.getBehaviour(AbstractComputerBehaviour.TYPE) instanceof ComputerBehaviour behaviour) {
                if (behaviour.peripheral != null)
                    return behaviour.peripheral;
                return behaviour.peripheral = factory.apply(blockEntity);
            }
            return null;
        }, type);
    }
}
