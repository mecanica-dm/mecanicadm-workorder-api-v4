package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.material.usecase.command.UpdateMaterialCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMaterialUseCaseTest {

    @Mock
    private MaterialGateway gateway;

    @InjectMocks
    private UpdateMaterialUseCase updateMaterialUseCase;

    private UUID materialId;
    private UpdateMaterialCommand command;
    private Material existingMaterial;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();
        command = new UpdateMaterialCommand(
                materialId,
                "Pneu Novo",
                "Marca Nova",
                "Descrição Nova",
                new BigDecimal("300.00"),
                MaterialType.CONSUMABLE
        );
        existingMaterial = Material.create(
                "Pneu Velho",
                "Marca Velha",
                "Descrição Velha",
                new BigDecimal("200.00"),
                MaterialType.PART
        );
    }

    @Test
    @DisplayName("Deve atualizar um material existente com sucesso")
    void shouldUpdateExistingMaterialSuccessfully() {
        when(gateway.findById(materialId)).thenReturn(Optional.of(existingMaterial));
        when(gateway.update(any(Material.class))).thenReturn(existingMaterial);

        updateMaterialUseCase.execute(command);

        verify(gateway).findById(materialId);
        verify(gateway).update(existingMaterial);

        assertEquals(command.name(), existingMaterial.getName());
        assertEquals(command.brand(), existingMaterial.getBrand());
        assertEquals(command.price(), existingMaterial.getPrice());
        assertEquals(command.type(), existingMaterial.getType());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o material não for encontrado para atualização")
    void shouldThrowExceptionWhenMaterialNotFound() {
        when(gateway.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(MaterialExceptions.MaterialNotFound.class, () -> updateMaterialUseCase.execute(command));

        verify(gateway).findById(materialId);
        verify(gateway, never()).create(any(Material.class));
    }
}
