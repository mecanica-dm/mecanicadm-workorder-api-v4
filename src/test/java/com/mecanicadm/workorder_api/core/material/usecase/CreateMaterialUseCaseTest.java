package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.usecase.command.CreateMaterialCommand;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.AddStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.command.AddStockCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMaterialUseCaseTest {

    @Mock
    private MaterialGateway gateway;

    @Mock
    private AddStockUseCase addStockUseCase;

    @InjectMocks
    private CreateMaterialUseCase createMaterialUseCase;

    private CreateMaterialCommand command;

    @BeforeEach
    void setUp() {
        command = new CreateMaterialCommand(
                "Pastilha de Freio",
                "Brembo",
                "Pastilha cerâmica",
                new BigDecimal("250.00"),
                MaterialType.PART,
                10
        );
    }

    @Test
    @DisplayName("Deve criar um material com sucesso e disparar a adição de estoque")
    void shouldCreateMaterialSuccessfully() {
        UUID expectedId = UUID.randomUUID();
        Material savedMaterial = mock(Material.class);
        when(savedMaterial.getId()).thenReturn(expectedId);
        when(gateway.create(any(Material.class))).thenReturn(savedMaterial);

        UUID resultId = createMaterialUseCase.execute(command);

        assertNotNull(resultId);
        assertEquals(expectedId, resultId);
        verify(gateway).create(any(Material.class));
        verify(addStockUseCase).execute(new AddStockCommand(expectedId, 10));
    }
}
