package com.mecanicadm.workorder_api.core.material.usecase;

import com.mecanicadm.workorder_api.core.material.domain.Material;
import com.mecanicadm.workorder_api.core.material.domain.enums.MaterialType;
import com.mecanicadm.workorder_api.core.material.domain.port.MaterialGateway;
import com.mecanicadm.workorder_api.core.material.exception.MaterialExceptions;
import com.mecanicadm.workorder_api.core.material.usecase.query.GetMaterialByIdQuery;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMaterialByIdUseCaseTest {

    @Mock
    private MaterialGateway repository;

    @InjectMocks
    private GetMaterialByIdUseCase getMaterialByIdUseCase;

    @Test
    @DisplayName("Deve retornar um material quando o ID existir")
    void shouldReturnMaterialWhenIdExists() {
        UUID id = UUID.randomUUID();
        Material material = Material.restore(id, "Pneu", "Michelin", "Pneu de carro", new BigDecimal("500"), MaterialType.PART, null, null, null);

        when(repository.findById(id)).thenReturn(Optional.of(material));

        Material response = getMaterialByIdUseCase.execute(new GetMaterialByIdQuery(id));

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Pneu", response.getName());
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o material não for encontrado")
    void shouldThrowExceptionWhenMaterialNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MaterialExceptions.MaterialNotFound.class, () -> getMaterialByIdUseCase.execute(new GetMaterialByIdQuery(id)));
        verify(repository).findById(id);
    }
}
