package org.antarcticgardens.cna.content.motor.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class AdvancedMotorVariant implements IMotorVariant {
    @Override
    public long getMaxCapacity() {
        return CNAConfig.server(cfg -> cfg.advancedMotorCapacity);
    }

    @Override
    public float getSpeed() {
        return CNAConfig.server(cfg -> cfg.advancedMotorSpeed).floatValue();
    }

    @Override
    public float getStress() {
        return CNAConfig.server(cfg -> cfg.advancedMotorStress).floatValue();
    }
}
