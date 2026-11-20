package org.antarcticgardens.cna.content.motor.extension.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class AdvancedMotorExtensionVariant implements IMotorExtensionVariant {
    @Override
    public float getMultiplier() {
        return CNAConfig.server(cfg -> cfg.advancedMotorExtensionMultiplier).floatValue();
    }

    @Override
    public long getExtraCapacity() {
        return CNAConfig.server(cfg -> cfg.advancedMotorExtensionExtraCapacity);
    }

    @Override
    public int getScrollStep() {
        return CNAConfig.server(cfg -> cfg.advancedMotorExtensionScrollStep);
    }
}
