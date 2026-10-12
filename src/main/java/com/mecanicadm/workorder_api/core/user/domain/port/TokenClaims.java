package com.mecanicadm.workorder_api.core.user.domain.port;

import com.mecanicadm.workorder_api.core.user.domain.enums.TokenRole;

public record TokenClaims(String subject, TokenRole role) {
}