package org.antarcticgardens.cna.rendering;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** PORT-NOTE(26.1): Create Fly's KineticBlockEntityRenderer passes the render state to getRotatedModel. */
public class HalfShaftRenderer<T extends KineticBlockEntity> extends KineticBlockEntityRenderer<T, KineticBlockEntityRenderer.KineticRenderState> {
    public HalfShaftRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(T be, KineticRenderState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState);
    }
}
