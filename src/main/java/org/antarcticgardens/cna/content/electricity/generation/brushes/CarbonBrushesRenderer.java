package org.antarcticgardens.cna.content.electricity.generation.brushes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.CNAPartialModels;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;

/** PORT-NOTE(26.1): extract/submit; the shaft as Create Fly's ShaftRenderer, plus the spinning coil. */
public class CarbonBrushesRenderer implements BlockEntityRenderer<CarbonBrushesBlockEntity, CarbonBrushesRenderer.BrushesRenderState> {
    public CarbonBrushesRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class BrushesRenderState extends SingleKineticRenderState {
        public @Nullable SuperByteBufferRenderState coil;
    }

    @Override
    public BrushesRenderState createRenderState() {
        return new BrushesRenderState();
    }

    @Override
    public void extractRenderState(CarbonBrushesBlockEntity be, BrushesRenderState state, float partialTicks, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, breakProgress);
        Direction.Axis axis = getRotationAxisOf(state.blockState);
        // With Flywheel the shaft is a visual; this renderer still runs for the extras (renderNormally, as upstream).
        state.model = null;
        if (!VisualizationManager.supportsVisualization(level)) {
            state.model = CachedBuffers.block(KINETIC_BLOCK, shaft(axis)).cardinalLighting(level).light(state.lightCoords)
                .color(getTintColor(be)).extractRenderState();
            state.angle = getRotateAngleWithoutBeOffset(axis, be, state, level);
        }

        // Upstream: standardKineticRotationTransform, then the coil model (built along Y) turned onto the facing axis.
        Direction dir = state.blockState.getValue(BlockStateProperties.FACING);
        SuperByteBuffer coil = CachedBuffers.partial(CNAPartialModels.COIL, state.blockState);
        Quaternionf spin = getRotateAngleForBe(dir.getAxis(), dir.getAxis().getPositive(), be, state, level);
        if (spin != null)
            coil.rotateCentered(spin);
        coil.center();
        switch (dir.getAxis()) {
            case X -> coil.rotateZDegrees(90);
            case Z -> coil.rotateXDegrees(90);
            default -> {
            }
        }
        coil.uncenter();
        state.coil = coil.cardinalLighting(level).light(state.lightCoords).color(getTintColor(be)).extractRenderState();
    }

    @Override
    public void submit(BrushesRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (state.coil != null)
            state.coil.submit(matrices, queue);
        if (state.model != null) {
            matrices.pushPose();
            state.submit(matrices, queue);
            matrices.popPose();
        }
    }
}
