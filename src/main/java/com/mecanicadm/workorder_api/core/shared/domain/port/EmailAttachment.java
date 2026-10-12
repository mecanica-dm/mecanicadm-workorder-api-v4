package com.mecanicadm.workorder_api.core.shared.domain.port;

import java.util.Arrays;
import java.util.Objects;

public record EmailAttachment(String fileName, byte[] content) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAttachment(String name, byte[] content1))) return false;
        return Objects.equals(fileName, name)
                && Arrays.equals(content, content1);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hashCode(fileName);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "EmailAttachment[fileName=%s, content.length=%d]".formatted(fileName, content != null ? content.length : 0);
    }
}
