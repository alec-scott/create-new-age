package org.antarcticgardens.cna.content.motor;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerKineticScrollValueBehaviour;

/**
 * PORT-NOTE(26.1): server half of the motor speed setting (Create Fly splits scroll values: the setting lives on the
 * block entity, the value box and board are a client behaviour, see client/CNAMotorValueBoxes). The two-row
 * (direction) value settings come from ServerKineticScrollValueBehaviour, as upstream.
 */
public class MotorScrollValueBehaviour extends ServerKineticScrollValueBehaviour {
    public MotorScrollValueBehaviour(SmartBlockEntity be) {
        super(be);
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

    public int getMin() {
        return min;
    }
}
