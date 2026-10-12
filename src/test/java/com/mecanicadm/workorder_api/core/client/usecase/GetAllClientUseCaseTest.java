package com.mecanicadm.workorder_api.core.client.usecase;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageQuery;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageResult;
import com.mecanicadm.workorder_api.core.client.usecase.query.GetAllClientQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllClientUseCaseTest {

    @Mock
    private ClientGateway repository;

    private GetAllClientUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAllClientUseCase(repository);
    }

    @Test
    @DisplayName("Deve listar os clientes com sucesso usando filtros e paginação")
    void shouldGetAllClientsSuccessfully() {
        GetAllClientQuery query = new GetAllClientQuery(null, null, 0, 10, "name", "ASC");
        ClientPageResult expectedPage = new ClientPageResult(List.of(mock(Client.class)), 1);

        when(repository.findAll(argThat(q -> q.page() == 0 && q.size() == 10 && q.sortBy().equals("name") && q.direction().equals("ASC")))).thenReturn(expectedPage);

        ClientPageResult result = useCase.execute(query);

        assertNotNull(result);
        assertEquals(1, result.items().size());
        verify(repository).findAll(any(ClientPageQuery.class));
    }

    @Test
    @DisplayName("Deve aplicar filtro de documento quando fornecido na query")
    void shouldApplyDocumentFilter() {
        GetAllClientQuery query = new GetAllClientQuery(null, "17871234053", 0, 10, "name", "ASC");
        ClientPageResult expectedPage = new ClientPageResult(List.of(mock(Client.class)), 1);

        when(repository.findAll(any(ClientPageQuery.class))).thenReturn(expectedPage);

        ClientPageResult result = useCase.execute(query);

        assertNotNull(result);
        verify(repository).findAll(argThat(q -> "17871234053".equals(q.filter().document())));
    }

    @Test
    @DisplayName("Deve aplicar filtro de nome quando fornecido na query")
    void shouldApplyNameFilter() {
        GetAllClientQuery query = new GetAllClientQuery("Jo\u00E3o", null, 0, 10, "name", "ASC");
        ClientPageResult expectedPage = new ClientPageResult(List.of(mock(Client.class)), 1);

        when(repository.findAll(any(ClientPageQuery.class))).thenReturn(expectedPage);

        ClientPageResult result = useCase.execute(query);

        assertNotNull(result);
        verify(repository).findAll(argThat(q -> "Jo\u00E3o".equals(q.filter().name())));
    }
}
