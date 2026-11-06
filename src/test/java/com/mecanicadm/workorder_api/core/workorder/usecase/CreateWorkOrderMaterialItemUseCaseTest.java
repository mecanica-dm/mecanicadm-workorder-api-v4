package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.DeductStockUseCase;
import com.mecanicadm.workorder_api.core.workorder.domain.WorkOrderMaterialItem;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkOrderMaterialItemUseCaseTest {

    @Mock
    private MaterialGateway materialGateway;

    @Mock
    private DeductStockUseCase deductStockUseCase;

    @InjectMocks
    private CreateWorkOrderMaterialItemUseCase useCase;

    @Test
    @DisplayName("Deve criar um item de material na ordem de serviço com sucesso")
    void shouldCreateWorkOrderMaterialItemSuccessfully() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        int quantity = 2;
        Material material = mock(Material.class);
        when(material.getId()).thenReturn(materialId);
        when(materialGateway.findById(materialId)).thenReturn(Optional.of(material));

        WorkOrderMaterialItem result = useCase.execute(new CreateWorkOrderMaterialItemCommand(workOrderId, materialId, quantity));

        assertNotNull(result);
        verify(materialGateway).findById(materialId);
        verify(deductStockUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o material não for encontrado")
    void shouldThrowExceptionWhenMaterialNotFound() {
        UUID materialId = UUID.randomUUID();
        UUID workOrderId = UUID.randomUUID();
        int quantity = 1;
        when(materialGateway.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(MaterialExceptions.MaterialNotFound.class,
                () -> useCase.execute(new CreateWorkOrderMaterialItemCommand(workOrderId, materialId, quantity)));

        verify(materialGateway).findById(materialId);
        verify(deductStockUseCase, never()).execute(any());
    }
}
