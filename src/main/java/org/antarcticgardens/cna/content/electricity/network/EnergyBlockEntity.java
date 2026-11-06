package org.antarcticgardens.cna.content.electricity.network;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.antarcticgardens.cna.port.esl.energy.EnergyStorage;

public record EnergyBlockEntity(BlockEntity entity, EnergyStorage storage) {
}
