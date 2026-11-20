package org.antarcticgardens.cna.content.motor.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class ReinforcedMotorVariant implements IMotorVariant {
    @Override
    public long getMaxCapacity() {
        return CNAConfig.server(cfg -> cfg.reinforcedMotorCapacity);
    }

    @Override
    public float getSpeed() {
        return CNAConfig.server(cfg -> cfg.reinforcedMotorSpeed).floatValue();
    }

    @Override
    public float getStress() {
        return CNAConfig.server(cfg -> cfg.reinforcedMotorStress).floatValue();
    }
}
