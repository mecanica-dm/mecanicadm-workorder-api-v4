package com.mecanicadm.workorder_api.infra.config.jackson;

public class ExcludeZeroFilter {
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Number number) {
            return number.doubleValue() == 0.0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ExcludeZeroFilter.class.hashCode();
    }
}
