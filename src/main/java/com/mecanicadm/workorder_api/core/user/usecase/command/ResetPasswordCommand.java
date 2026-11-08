package com.mecanicadm.workorder_api.core.user.usecase.command;

public record ResetPasswordCommand(String token, String newPassword) { }
