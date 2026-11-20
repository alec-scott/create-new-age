package org.antarcticgardens.cna.content.motor.extension.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class BasicMotorExtensionVariant implements IMotorExtensionVariant {
    @Override
    public float getMultiplier() {
        return CNAConfig.server(cfg -> cfg.basicMotorExtensionMultiplier).floatValue();
    }

    @Override
    public long getExtraCapacity() {
        return CNAConfig.server(cfg -> cfg.basicMotorExtensionExtraCapacity);
    }

    @Override
    public int getScrollStep() {
        return CNAConfig.server(cfg -> cfg.basicMotorExtensionScrollStep);
    }
}
