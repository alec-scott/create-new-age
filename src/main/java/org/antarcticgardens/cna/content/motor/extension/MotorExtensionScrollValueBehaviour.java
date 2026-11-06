package org.antarcticgardens.cna.content.motor.extension;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.world.entity.player.Player;

/**
 * PORT-NOTE(26.1): server half of the stress multiplier setting; the board (one "%" row in steps of {@link #step})
 * is client/CNAMotorValueBoxes.Extension.
 */
public class MotorExtensionScrollValueBehaviour extends ServerScrollValueBehaviour {
    protected int step;

    public MotorExtensionScrollValueBehaviour(SmartBlockEntity be, int step) {
        super(be);
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = Math.max(1, valueSetting.value());
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(value * step);
    }

    // PORT-NOTE(26.1): upstream divided the stored value by the step while building the board; the board's value is
    // in steps, so the settings are reported in steps here instead of mutating the value.
    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(0, Math.abs(value) / Math.max(1, step));
    }

    /** Sets the value without clamping, callbacks or sync (upstream assigned the field directly). */
    public void setValueRaw(int value) {
        this.value = value;
    }

    public void betweenValidated(int min, int max) {
        this.between(min, max);

        if (value > max) {
            value = max;
        } else if (value < min) {
            value = min;
        }
    }

    @Override
    public String getClipboardKey() {
        return "Stress Multiplier";
    }
}
