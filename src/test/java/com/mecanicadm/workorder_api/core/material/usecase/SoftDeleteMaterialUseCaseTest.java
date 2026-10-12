package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.material.usecase.command.SoftDeleteMaterialCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoftDeleteMaterialUseCaseTest {

    @Mock
    private MaterialGateway gateway;

    @InjectMocks
    private SoftDeleteMaterialUseCase softDeleteMaterialUseCase;

    @Test
    @DisplayName("Deve deletar um material existente com sucesso")
    void shouldDeleteExistingMaterialSuccessfully() {
        UUID materialId = UUID.randomUUID();
        Material material = Material.create("Pneu", "Michelin", "Pneu de carro", new BigDecimal("500"), MaterialType.PART);
        when(gateway.findById(materialId)).thenReturn(Optional.of(material));

        softDeleteMaterialUseCase.execute(new SoftDeleteMaterialCommand(materialId));

        verify(gateway).findById(materialId);
        verify(gateway).update(material);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o material não for encontrado para exclusão")
    void shouldThrowExceptionWhenMaterialNotFound() {
        UUID materialId = UUID.randomUUID();
        when(gateway.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(MaterialExceptions.MaterialNotFound.class, () -> softDeleteMaterialUseCase.execute(new SoftDeleteMaterialCommand(materialId)));

        verify(gateway).findById(materialId);
        verify(gateway, never()).update(any());
    }
}
