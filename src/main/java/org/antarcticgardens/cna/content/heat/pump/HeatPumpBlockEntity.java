package org.antarcticgardens.cna.content.heat.pump;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.util.StringFormatUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeatPumpBlockEntity extends SmartBlockEntity implements HeatBlockEntity, IHaveGoggleInformation {
    // PORT-NOTE(26.1): was a plain BlockEntity; Create Fly only shows goggle tooltips through a client TooltipBehaviour,
    // which needs a SmartBlockEntity (client/CNABlockEntityBehaviours). NBT moved to read/write (synced by Create Fly).
    @Override
    public void addBehaviours(java.util.List<com.zurrtum.create.api.behaviour.BlockEntityBehaviour<?>> behaviours) {
    }

    public HeatPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public float heat = 0;

    public float lastPump = 0;

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heat = tag.getFloatOr("heat", 0);
        lastPump = tag.getFloatOr("last", 0);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("heat", heat);
        tag.putFloat("last", lastPump);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("tooltip.create_new_age.pump").style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
        CreateLang.translate("tooltip.create_new_age.temperature.ps", StringFormatUtil.formatFloat(lastPump)).style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
        return true;
    }

    @Override
    public float getHeat() {
        return heat;
    }

    @Override
    public void addHeat(float amount) {
        heat += amount;
        setChanged();
    }

    @Override
    public void setHeat(float amount) {
        heat = amount;
        setChanged();
    }

    @Override
    public boolean canAdd(Direction from) {
        if (getLevel() == null)
            return false;
        return from != getLevel().getBlockState(getBlockPos()).getValue(HeatPumpBlock.FACING).getOpposite();
    }
}
