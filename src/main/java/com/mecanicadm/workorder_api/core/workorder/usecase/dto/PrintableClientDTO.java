package com.mecanicadm.workorder_api.core.workorder.usecase.dto;

public record PrintableClientDTO(
        String name,
        String document,
        String phone,
        String email
) {}
