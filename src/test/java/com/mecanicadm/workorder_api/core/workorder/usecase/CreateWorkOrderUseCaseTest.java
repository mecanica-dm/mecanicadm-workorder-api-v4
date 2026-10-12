package com.mecanicadm.workorder_api.core.workorder.usecase;

import com.mecanicadm.workorder_api.core.workorder.domain.port.WorkOrderGateway;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderCommand;
import com.mecanicadm.workorder_api.core.workorder.usecase.command.CreateWorkOrderMaterialItemCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkOrderUseCaseTest {

    @Mock
    private WorkOrderGateway gateway;
    @Mock
    private AddLaborToWorkOrderUseCase addLaborToWorkOrderUseCase;
    @Mock
    private AddMaterialToWorkOrderUseCase addMaterialToWorkOrderUseCase;

    @InjectMocks
    private CreateWorkOrderUseCase useCase;

    @Test
    @DisplayName("Deve criar uma ordem de serviço com sucesso")
    void shouldCreateWorkOrderSuccessfully() {
        UUID clientId = UUID.randomUUID();
        String vehicleId = "ABC-1234";
        String description = "Troca de oleo";
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(clientId, vehicleId, description, null, null);

        when(gateway.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultId = useCase.execute(command);

        assertNotNull(resultId);
        verify(gateway).create(any());
        verifyNoInteractions(addLaborToWorkOrderUseCase);
        verifyNoInteractions(addMaterialToWorkOrderUseCase);
    }

    @Test
    @DisplayName("Deve criar OS com serviços de mão de obra")
    void shouldCreateWorkOrderWithLaborItems() {
        UUID clientId = UUID.randomUUID();
        UUID laborId1 = UUID.randomUUID();
        UUID laborId2 = UUID.randomUUID();
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                clientId, "ABC-1234", "Troca de oleo", List.of(laborId1, laborId2), null);

        when(gateway.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultId = useCase.execute(command);

        assertNotNull(resultId);
        verify(gateway).create(any());
        verify(addLaborToWorkOrderUseCase, times(2)).execute(any());
        verifyNoInteractions(addMaterialToWorkOrderUseCase);
    }

    @Test
    @DisplayName("Deve criar OS com itens de material")
    void shouldCreateWorkOrderWithMaterialItems() {
        UUID clientId = UUID.randomUUID();
        UUID materialId1 = UUID.randomUUID();
        UUID materialId2 = UUID.randomUUID();
        var materialItems = List.of(
                new CreateWorkOrderMaterialItemCommand(null, materialId1, 2),
                new CreateWorkOrderMaterialItemCommand(null, materialId2, 1));
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                clientId, "ABC-1234", "Troca de oleo", null, materialItems);

        when(gateway.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultId = useCase.execute(command);

        assertNotNull(resultId);
        verify(gateway).create(any());
        verifyNoInteractions(addLaborToWorkOrderUseCase);
        verify(addMaterialToWorkOrderUseCase, times(2)).execute(any());
    }

    @Test
    @DisplayName("Deve criar OS com serviços e materiais")
    void shouldCreateWorkOrderWithLaborAndMaterialItems() {
        UUID clientId = UUID.randomUUID();
        UUID laborId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        var materialItems = List.of(
                new CreateWorkOrderMaterialItemCommand(null, materialId, 3));
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                clientId, "ABC-1234", "Troca de oleo", List.of(laborId), materialItems);

        when(gateway.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultId = useCase.execute(command);

        assertNotNull(resultId);
        verify(gateway).create(any());
        verify(addLaborToWorkOrderUseCase, times(1)).execute(any());
        verify(addMaterialToWorkOrderUseCase, times(1)).execute(any());
    }

    @Test
    @DisplayName("Deve criar OS com listas vazias de serviços e materiais")
    void shouldCreateWorkOrderWithEmptyLists() {
        UUID clientId = UUID.randomUUID();
        CreateWorkOrderCommand command = new CreateWorkOrderCommand(
                clientId, "ABC-1234", "Troca de oleo", List.of(), List.of());

        when(gateway.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UUID resultId = useCase.execute(command);

        assertNotNull(resultId);
        verify(gateway).create(any());
        verifyNoInteractions(addLaborToWorkOrderUseCase);
        verifyNoInteractions(addMaterialToWorkOrderUseCase);
    }
}
