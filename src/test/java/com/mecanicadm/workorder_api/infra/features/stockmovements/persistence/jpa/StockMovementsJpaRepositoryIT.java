package com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.jpa;

import com.mecanicadm.workorder_api.core.stockmovements.domain.enums.MovementType;
import com.mecanicadm.workorder_api.infra.features.stockmovements.persistence.entity.StockMovementsJpaEntity;
import com.mecanicadm.workorder_api.testutils.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StockMovementsJpaRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private StockMovementsJpaRepository stockMovementsRepository;

    @ParameterizedTest
    @MethodSource("provideBalanceTestCases")
    @Sql(scripts = "/sql/stock_movements_test_data.sql")
    @DisplayName("Deve calcular o saldo do material corretamente")
    void shouldCalculateBalanceCorrectly(UUID materialId, int expectedBalance) {
        Integer balance = stockMovementsRepository.getCurrentBalanceByMaterialId(materialId);

        assertEquals(expectedBalance, balance);
    }

    private static Stream<Arguments> provideBalanceTestCases() {
        return Stream.of(
                Arguments.of(UUID.fromString("770e8400-e29b-41d4-a716-446655440001"), 10),
                Arguments.of(UUID.fromString("00000000-0000-0000-0000-000000000000"), 0),
                Arguments.of(UUID.fromString("990e8400-e29b-41d4-a716-446655449999"), 50)
        );
    }

    @Test
    @DisplayName("Deve buscar todas as movimentacoes de um material ordenadas por data descrescente")
    @Sql(scripts = "/sql/stock_movements_data.sql")
    void shouldFindAllMovementsOrderedByDateDesc() {
        UUID materialId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        List<StockMovementsJpaEntity> results = stockMovementsRepository.findAllByMaterialIdOrderByDateCreatedDesc(materialId);

        assertEquals(2, results.size());
        assertEquals(MovementType.REDUCTION, results.get(0).getType());
        assertEquals(MovementType.ADDITION, results.get(1).getType());
    }

    @Test
    @DisplayName("Deve calcular o saldo atual do material")
    @Sql(scripts = "/sql/stock_movements_data.sql")
    void shouldCalculateCurrentBalance() {
        UUID materialId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Integer balance = stockMovementsRepository.getCurrentBalanceByMaterialId(materialId);

        assertEquals(7, balance);
    }
}
