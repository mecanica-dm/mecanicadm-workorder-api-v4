package com.mecanicadm.workorder_api.infra.features.stockmovements.api;

import com.mecanicadm.workorder_api.core.stockmovements.usecase.GetStockStatementUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.query.GetStockStatementQuery;
import com.mecanicadm.workorder_api.infra.features.stockmovements.api.dto.response.StockStatementResponse;
import com.mecanicadm.workorder_api.infra.features.stockmovements.api.openapi.StockMovementsOpenApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/stock-movements")
public class StockMovementsController implements StockMovementsOpenApi {

    private final GetStockStatementUseCase getStockStatementUseCase;

    public StockMovementsController(GetStockStatementUseCase getStockStatementUseCase) {
        this.getStockStatementUseCase = getStockStatementUseCase;
    }

    @Override
    @GetMapping("/{materialId}/statement")
    public ResponseEntity<StockStatementResponse> getStatement(@PathVariable UUID materialId) {
        var statement = getStockStatementUseCase.execute(new GetStockStatementQuery(materialId));
        return ResponseEntity.ok(StockStatementResponse.from(statement));
    }
}
