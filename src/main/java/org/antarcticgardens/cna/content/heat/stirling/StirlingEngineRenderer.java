package org.antarcticgardens.cna.content.heat.stirling;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * PORT-NOTE(26.1): extract/submit. The shaft is Create Fly's kinetic path (skipped under Flywheel, where the visual
 * draws it); the small flywheel is drawn here when visualization is supported, as upstream.
 */
public class StirlingEngineRenderer extends KineticBlockEntityRenderer<StirlingEngineBlockEntity, StirlingEngineRenderer.StirlingRenderState> {
    public StirlingEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static class StirlingRenderState extends KineticRenderState {
        public @Nullable SuperByteBufferRenderState wheel;
    }

    @Override
    public StirlingRenderState createRenderState() {
        return new StirlingRenderState();
    }

    @Override
    public void extractRenderState(StirlingEngineBlockEntity be, StirlingRenderState state, float partialTicks, Vec3 cameraPos,
                                   @Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.wheel = null;
        Level level = be.getLevel();
        if (!VisualizationManager.supportsVisualization(level))
            return;
        if (!state.support)
            return;

        updateBaseRenderState(be, state, level, crumblingOverlay);
        Direction.Axis axis = be.getBlockState().getValue(BlockStateProperties.AXIS);
        BlockState wheelState = AllBlocks.FLYWHEEL.defaultBlockState().setValue(BlockStateProperties.AXIS, axis);

        float speed = be.visualSpeed.getValue(partialTicks) * 3 / 10f;
        float angle = be.angle + speed * partialTicks;

        state.wheel = CachedBuffers.block(wheelState)
                .rotateCentered(AngleHelper.rad(angle), Direction.get(Direction.AxisDirection.POSITIVE, axis))
                .center().scale(0.2f, 0.2f, 0.2f).uncenter()
                .light(SmartBlockEntityRenderer.getLightCoords(level, be.getBlockPos()))
                .extractRenderState();
    }

    @Override
    public void submit(StirlingRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        super.submit(state, matrices, queue, cameraState);
        if (state.wheel != null)
            state.wheel.submit(matrices, queue);
    }

    @Override
    protected BlockState getRenderedBlockState(StirlingEngineBlockEntity be) {
        return AllBlocks.SHAFT.defaultBlockState().setValue(BlockStateProperties.AXIS,
                be.getBlockState().getValue(BlockStateProperties.AXIS));
    }
}
