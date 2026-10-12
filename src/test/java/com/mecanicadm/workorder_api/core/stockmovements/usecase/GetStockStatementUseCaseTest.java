package com.mecanicadm.workorder_api.core.stockmovements.usecase;

import com.mecanicadm.workorder_api.core.stockmovements.domain.StockMovements;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsFilter;
import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.dto.StockStatement;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.query.GetStockStatementQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetStockStatementUseCaseTest {

    @Mock
    private StockMovementsGateway gateway;

    @InjectMocks
    private GetStockStatementUseCase getStockStatementUseCase;

    @Test
    @DisplayName("Deve retornar o extrato de estoque corretamente")
    void shouldReturnStockStatementCorrectly() {
        UUID materialId = UUID.randomUUID();
        GetStockStatementQuery query = new GetStockStatementQuery(materialId);

        StockMovements addition = StockMovements.recordAddition(materialId, 10);
        StockMovements reduction = StockMovements.recordReduction(materialId, UUID.randomUUID(), 3);

        when(gateway.getCurrentBalanceByMaterialId(materialId)).thenReturn(7);
        when(gateway.findAllByMaterialIdOrderByDateCreatedDesc(new StockMovementsFilter(materialId)))
                .thenReturn(List.of(addition, reduction));

        StockStatement result = getStockStatementUseCase.execute(query);

        assertNotNull(result);
        assertEquals(materialId, result.materialId());
        assertEquals(7, result.currentBalance());
        assertEquals(2, result.movements().size());
    }
}
