package org.antarcticgardens.cna.content.electricity.connector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;
import org.antarcticgardens.cna.content.electricity.wire.WireType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * PORT-NOTE(26.1): extract/submit. Extraction builds each wire's quads ({@link Wire#render}) with their light; submit
 * replays them through {@code submitCustomGeometry} in vanilla's no-cull entity cutout type (was a custom solid
 * render type with the block vertex format). The render bounding box moved to the block entity.
 */
public class ElectricalConnectorRenderer<T extends AbstractElectricalConnector> implements BlockEntityRenderer<T, ElectricalConnectorRenderer.WireRenderState> {
    public ElectricalConnectorRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    public record WireMesh(Identifier texture, List<float[]> vertices) {
    }

    public static class WireRenderState extends BlockEntityRenderState {
        public final List<WireMesh> wires = new ArrayList<>();
    }

    @Override
    public WireRenderState createRenderState() {
        return new WireRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, WireRenderState state, float partialTick, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        SmartBlockEntityRenderer.extractBase(blockEntity, state, crumblingOverlay);
        state.wires.clear();
        Level level = blockEntity.getLevel();
        if (level == null)
            return;
        blockEntity.getConnectorPositions().forEach((key, value) ->
                extractConnection(blockEntity.getBlockPos(), key, value, level, state.wires));
        extractHand(blockEntity, partialTick, state.wires);
    }

    @Override
    public void submit(WireRenderState state, PoseStack poseStack, SubmitNodeCollector queue, CameraRenderState camera) {
        for (WireMesh wire : state.wires) {
            queue.submitCustomGeometry(poseStack, RenderTypes.entityCutout(wire.texture()), (pose, consumer) -> {
                for (float[] v : wire.vertices()) {
                    consumer.addVertex(pose.pose(), v[0], v[1], v[2])
                            .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                            .setUv(v[3], v[4])
                            .setOverlay(OverlayTexture.NO_OVERLAY)
                            .setLight(Float.floatToRawIntBits(v[5]))
                            .setNormal(pose, 0.0f, 1.0f, 0.0f);
                }
            });
        }
    }

    //Makes sure that only one of the two connectors renders the wire
    public boolean shouldRenderConnection(BlockPos pos, BlockPos endPos) {
        return pos.compareTo(endPos) < 0;
    }

    public void extractConnection(BlockPos pos, BlockPos endPos, WireType wireType, Level level, List<WireMesh> out) {
        if (!shouldRenderConnection(pos, endPos)) {
            return;
        }

        Identifier texture = wireType.getTextureLocation();

        var originConnectorPreCast = level.getBlockEntity(pos);
        var endConnectorPreCast = level.getBlockEntity(endPos);
        if (!(originConnectorPreCast instanceof AbstractElectricalConnector originConnector) ||
                !(endConnectorPreCast instanceof AbstractElectricalConnector endConnector)) {
            return;
        }

        var originPoint = originConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(pos));
        var endPoint = endConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(endPos));

        out.add(mesh(texture, originPoint, endPoint, level));
    }

    private static WireMesh mesh(Identifier texture, Vec3 start, Vec3 end, Level level) {
        Wire wire = new Wire(
                start.toVector3f(),
                end.toVector3f(),
                CNAConfig.getClient().wireSectionsPerMeter.get(),
                CNAConfig.getClient().wireThickness.get().floatValue(),
                CNAConfig.getServer().maxWireLength.get()
        );
        List<float[]> vertices = new ArrayList<>();
        wire.render(vertices, level);
        return new WireMesh(texture, vertices);
    }

    public void extractHand(AbstractElectricalConnector blockEntity, float partialTick, List<WireMesh> out) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        Level level = player.level();
        if (!minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }

        ItemStack itemStack = player.getMainHandItem();

        if (!(itemStack.getItem() instanceof ElectricWireItem)) {
            itemStack = player.getOffhandItem();
        }

        if (!(itemStack.getItem() instanceof ElectricWireItem wireItem)) {
            return;
        }

        BlockPos bound = wireItem.getBoundConnector(itemStack);

        if (bound == null || !bound.equals(blockEntity.getBlockPos())) {
            return;
        }

        Vec3 eyePos = minecraft.gameRenderer.getMainCamera().position();
        Vec3 viewVector = player.getViewVector(partialTick);
        Vec3 wireEnd = eyePos.add(viewVector.scale(player.blockInteractionRange()));
        var wireStart = blockEntity.getConnectionPoint().add(Vec3.atLowerCornerOf(bound));

        HitResult lookingAt = minecraft.hitResult;

        if (lookingAt instanceof BlockHitResult blockHit) {
            BlockPos lookedAtPos = blockHit.getBlockPos();
            BlockEntity lookedBlockEntity = level.getBlockEntity(lookedAtPos);
            boolean snapped = false;

            if (lookedBlockEntity != null) {
                if (lookedBlockEntity == blockEntity) {
                    return;
                }
                if (lookedBlockEntity instanceof AbstractElectricalConnector otherConnector) {
                    if (otherConnector.isConnected(blockEntity.getBlockPos())) {
                        return;
                    }

                    wireEnd = otherConnector.getConnectionPoint().add(Vec3.atLowerCornerOf(lookedAtPos));
                    snapped = true;
                }
            }

            if (!snapped) {
                Vec3 vec = blockHit.getLocation().subtract(viewVector.scale(0.1));

                if (eyePos.distanceToSqr(wireEnd) > eyePos.distanceToSqr(vec))
                    wireEnd = vec;
            }
        }

        Identifier texture = wireItem.getWireType().getTextureLocation();
        double distanceSqr = wireEnd.distanceToSqr(wireStart);
        int maxDistance = CNAConfig.getServer().maxWireLength.get();

        if (distanceSqr >= (maxDistance * maxDistance)) {
            if (distanceSqr > (maxDistance * maxDistance) * 4) {
                return;
            }

            texture = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "textures/wire/red.png");
        }

        out.add(mesh(texture, wireStart, wireEnd, level));
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
