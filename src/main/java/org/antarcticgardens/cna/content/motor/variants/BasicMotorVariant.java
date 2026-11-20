package org.antarcticgardens.cna.content.motor.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class BasicMotorVariant implements IMotorVariant {
    @Override
    public long getMaxCapacity() {
        return CNAConfig.server(cfg -> cfg.basicMotorCapacity);
    }

    @Override
    public float getSpeed() {
        return CNAConfig.server(cfg -> cfg.basicMotorSpeed).floatValue();
    }

    @Override
    public float getStress() {
        return CNAConfig.server(cfg -> cfg.basicMotorStress).floatValue();
    }
}
