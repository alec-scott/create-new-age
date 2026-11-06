package org.antarcticgardens.cna.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.client.content.kinetics.motor.MotorValueBox;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsBoard;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsFormatter;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.KineticScrollValueBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.content.kinetics.motor.CreativeMotorBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionScrollValueBehaviour;

/**
 * PORT-NOTE(26.1): client halves of the motor and motor extension value settings (were the full ScrollValueBehaviours
 * on the block entities). Registered per block entity type in {@link CNABlockEntityBehaviours}.
 */
public final class CNAMotorValueBoxes {
    private CNAMotorValueBoxes() {
    }

    /** Motor speed: Create's creative motor box and two-row board, sized to the motor's (config) speed limit. */
    public static class Motor extends KineticScrollValueBehaviour {
        public Motor(SmartBlockEntity be) {
            super(CreateLang.translateDirect("kinetics.creative_motor.rotation_speed"), be, new MotorValueBox());
            needsWrench = true;
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ImmutableList<Component> rows = ImmutableList.of(Component.literal("⟳").withStyle(ChatFormatting.BOLD),
                    Component.literal("⟲").withStyle(ChatFormatting.BOLD));
            ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
            int max = behaviour.getMax();
            return new ValueSettingsBoard(label, max, Math.max(1, max / 8), rows, formatter);
        }
    }

    /** Motor extension stress multiplier, in steps of the variant's scroll step. */
    public static class Extension extends ScrollValueBehaviour<SmartBlockEntity, MotorExtensionScrollValueBehaviour> {
        public Extension(SmartBlockEntity be) {
            super(CreateLang.translateDirect("scroll.create_new_age.motor.stress_multiplier"), be, new ExtensionValueBox());
            withFormatter(v -> String.valueOf(Math.abs(v)));
        }

        private int step() {
            return Math.max(1, behaviour.getStep());
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            ImmutableList<Component> rows = ImmutableList.of(Component.literal("%").withStyle(ChatFormatting.BOLD));
            ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
            return new ValueSettingsBoard(label, behaviour.getMax() / step(), 100, rows, formatter);
        }

        public MutableComponent formatSettings(ValueSettings settings) {
            return CreateLang.number(Math.max(1, Math.abs(settings.value() * step())))
                    .add(CreateLang.text("%").style(ChatFormatting.BOLD))
                    .component();
        }
    }

    static class ExtensionValueBox extends MotorValueBox {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 13.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(CreativeMotorBlock.FACING);
            return super.getLocalOffset(state).add(new Vec3(facing.step()).scale(2 / 16f));
        }
    }

    /** Street light level (0-15), wrench only, on the four horizontal sides. */
    public static class StreetLight extends ScrollValueBehaviour<SmartBlockEntity, com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour> {
        public StreetLight(SmartBlockEntity be) {
            super(CreateLang.translateDirect("create_new_age.street_light.light_level"), be, new StreetLightBox());
            needsWrench = true;
        }
    }

    static class StreetLightBox extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 9, 12.5);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction == Direction.UP || direction == Direction.DOWN)
                return false;
            return super.isSideActive(state, direction);
        }
    }
}
