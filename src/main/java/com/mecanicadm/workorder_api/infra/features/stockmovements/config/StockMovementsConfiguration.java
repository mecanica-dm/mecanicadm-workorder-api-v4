package com.mecanicadm.workorder_api.infra.features.stockmovements.config;

import com.mecanicadm.workorder_api.core.stockmovements.domain.port.StockMovementsGateway;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.AddStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.DeductStockUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.GetStockStatementUseCase;
import com.mecanicadm.workorder_api.core.stockmovements.usecase.SoftDeleteStockUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockMovementsConfiguration {

    @Bean
    public AddStockUseCase addStockUseCase(StockMovementsGateway gateway) {
        return new AddStockUseCase(gateway);
    }

    @Bean
    public DeductStockUseCase deductStockUseCase(StockMovementsGateway gateway) {
        return new DeductStockUseCase(gateway);
    }

    @Bean
    public SoftDeleteStockUseCase softDeleteStockUseCase(StockMovementsGateway gateway) {
        return new SoftDeleteStockUseCase(gateway);
    }

    @Bean
    public GetStockStatementUseCase getStockStatementUseCase(StockMovementsGateway gateway) {
        return new GetStockStatementUseCase(gateway);
    }
}
