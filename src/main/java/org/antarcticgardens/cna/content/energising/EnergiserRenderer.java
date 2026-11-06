package org.antarcticgardens.cna.content.energising;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;

/** PORT-NOTE(26.1): extract/submit; the shaft as Create Fly's ShaftRenderer, plus the charging beam. */
public class EnergiserRenderer implements BlockEntityRenderer<EnergiserBlockEntity, EnergiserRenderer.EnergiserRenderState> {
    public EnergiserRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class EnergiserRenderState extends SingleKineticRenderState {
        public @Nullable SuperByteBufferRenderState beam;
    }

    @Override
    public EnergiserRenderState createRenderState() {
        return new EnergiserRenderState();
    }

    @Override
    public void extractRenderState(EnergiserBlockEntity be, EnergiserRenderState state, float partialTicks, Vec3 cameraPosition,
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

        state.beam = null;
        if (be.size > 0f && level != null) {
            float scalar = (1 - be.size * 0.12f) * 0.5f;
            state.beam = CachedBuffers.block(Blocks.WHITE_CONCRETE.defaultBlockState())
                    .color(100, 150, 200, 200)
                    .translate(scalar, -1.2f, scalar)
                    .scale(be.size * 0.12f, 1.3f, be.size * 0.12f)
                    .extractRenderState();
        }
    }

    @Override
    public void submit(EnergiserRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (state.beam != null)
            state.beam.submit(RenderTypes.lightning(), matrices, queue);
        if (state.model != null) {
            matrices.pushPose();
            state.submit(matrices, queue);
            matrices.popPose();
        }
    }
}
