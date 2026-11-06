package org.antarcticgardens.cna.util;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;

/** PORT-NOTE(26.1): a plain server scroll value whose initial value can be set without clamping or callbacks. */
public class RawScrollValueBehaviour extends ServerScrollValueBehaviour {
    public RawScrollValueBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public void setValueRaw(int value) {
        this.value = value;
    }
}
