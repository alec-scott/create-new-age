package org.antarcticgardens.cna.content.energising;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.util.RunnableUtil;
import org.antarcticgardens.cna.util.StringFormatUtil;
import org.antarcticgardens.cna.port.esl.energy.EnergyStorage;
import org.antarcticgardens.cna.port.esl.energy.SimpleEnergyStorage;

import java.util.List;

public class EnergiserBlockEntity extends KineticBlockEntity implements com.zurrtum.create.client.api.goggles.IHaveGoggleInformation {
    private final SimpleEnergyStorage storage;

    public int tier;
    public float size = 0f;
    private EnergiserBehaviour energisingBehaviour;


    public EnergiserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        
        if (state.getBlock().equals(CNABlocks.BASIC_ENERGISER.get())) {
            tier = 1;
        } else if (state.getBlock().equals(CNABlocks.ADVANCED_ENERGISER.get())) {
            tier = 2;
        } else {
            tier = 3;
        }
        
        storage = new SimpleEnergyStorage(EnergiserBlock.getCapacity(tier))
                .onFinalCommit(RunnableUtil.createBlockEntityUpdater(this));
        
        
        this.energisingBehaviour.tier = tier;
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        compound.putLong("Energy", storage.getStoredEnergy());
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        storage.setStoredEnergy(compound.getLongOr("Energy", 0));
        super.read(compound, clientPacket);
    }

    protected AABB createRenderBoundingBox() {
        var pos = new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        return new AABB(pos.subtract(1, 3, 1), pos.add(1, 1, 1));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        energisingBehaviour = new EnergiserBehaviour(this);
        behaviours.add(energisingBehaviour);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public long lastCharged = -1;
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("tooltip.create_new_age.energy_stats")
                .style(ChatFormatting.WHITE).forGoggles(tooltip);

        CreateLang.translate("tooltip.create_new_age.energy_stored")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        CreateLang.translate("tooltip.create_new_age.energy_storage", StringFormatUtil.formatLong(storage.getStoredEnergy()), StringFormatUtil.formatLong(storage.getCapacity()))
                .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);

        if (lastCharged != -1) {
            CreateLang.translate("tooltip.create_new_age.energy_usage")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.translate("tooltip.create_new_age.energy_per_tick", StringFormatUtil.formatLong(lastCharged))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);
        }

        return true; // PORT-NOTE(26.1): kinetic stats come from the client CNAKineticTooltipBehaviour
    }
    
    public SimpleEnergyStorage getEnergyStorage() {
        return storage;
    }

    @Override
    public float calculateStressApplied() {
        float impact;
        if (this.tier == 1) {
            impact = 4.0f;
        } else if (this.tier == 2) {
            impact = 8.0f;
        } else {
            impact = 32.0f;
        }
        this.lastStressApplied = impact;
        return impact;
    }


    // PORT-NOTE(26.1): was in the constructor (ESL ignored repeats); on Fabric this registers TR Energy lookups, so
    // it runs once at init (CreateNewAge) instead of from both the server and client threads.
    public static void registerEnergyStorage() {
        EnergyStorage.registerForBlockEntity((blockEntity, direction) -> blockEntity.storage, CNABlockEntityTypes.ENERGISER.get());
    }
}
