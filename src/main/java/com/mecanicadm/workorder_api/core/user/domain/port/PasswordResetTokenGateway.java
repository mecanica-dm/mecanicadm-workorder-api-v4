package com.mecanicadm.workorder_api.core.user.domain.port;

import com.mecanicadm.workorder_api.core.user.domain.PasswordResetToken;
import com.mecanicadm.workorder_api.core.user.domain.User;

import java.util.Optional;

public interface PasswordResetTokenGateway {
    void deleteByUser(User user);
    void save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    void delete(PasswordResetToken token);
}
