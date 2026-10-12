package com.mecanicadm.workorder_api.infra.features.client.persistence.jpa;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientFilter;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageQuery;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageResult;
import com.mecanicadm.workorder_api.infra.features.client.persistence.entity.ClientJpaEntity;
import com.mecanicadm.workorder_api.infra.features.client.persistence.jpa.specification.ClientSpecificationBuilder;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRepositoryImplTest {

    @Mock
    private ClientJpaRepository jpaRepository;

    private ClientRepositoryImpl repository;
    private UUID id;
    private Client domain;
    private ClientJpaEntity entity;

    @BeforeEach
    void setUp() {
        repository = new ClientRepositoryImpl(jpaRepository);

        id = UUID.randomUUID();
        domain = mock(Client.class);
        lenient().when(domain.getId()).thenReturn(id);

        entity = new ClientJpaEntity(id, "Nome", "email@test.com", "12345678901", "11999999999");
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void shouldCreateClientSuccessfully() {
        try (MockedStatic<ClientJpaMapper> mapper = mockStatic(ClientJpaMapper.class)) {
            mapper.when(() -> ClientJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> ClientJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Client result = repository.create(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar cliente nulo")
    void shouldThrowExceptionWhenCreatingNullClient() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void shouldUpdateClientSuccessfully() {
        try (MockedStatic<ClientJpaMapper> mapper = mockStatic(ClientJpaMapper.class)) {
            mapper.when(() -> ClientJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> ClientJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            Client result = repository.update(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar cliente nulo")
    void shouldThrowExceptionWhenUpdatingNullClient() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void shouldFindClientById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ClientJpaMapper> mapper = mockStatic(ClientJpaMapper.class)) {
            mapper.when(() -> ClientJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<Client> result = repository.findById(id);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findById(id);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar cliente por ID inexistente")
    void shouldReturnEmptyWhenClientNotFoundById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Client> result = repository.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve verificar existencia de documento")
    void shouldCheckDocumentExistence() {
        when(jpaRepository.existsByDocument("123")).thenReturn(true);

        assertTrue(repository.existsByDocument("123"));
        verify(jpaRepository).existsByDocument("123");
    }

    @Test
    @DisplayName("Deve verificar inexistencia de documento")
    void shouldCheckDocumentNonExistence() {
        when(jpaRepository.existsByDocument("123")).thenReturn(false);

        assertFalse(repository.existsByDocument("123"));
    }

    @Test
    @DisplayName("Deve verificar existencia de email")
    void shouldCheckEmailExistence() {
        when(jpaRepository.existsByEmail("a@b.com")).thenReturn(true);

        assertTrue(repository.existsByEmail("a@b.com"));
        verify(jpaRepository).existsByEmail("a@b.com");
    }

    @Test
    @DisplayName("Deve verificar inexistencia de email")
    void shouldCheckEmailNonExistence() {
        when(jpaRepository.existsByEmail("a@b.com")).thenReturn(false);

        assertFalse(repository.existsByEmail("a@b.com"));
    }

    @Test
    @DisplayName("Deve verificar existencia de documento ignorando ID")
    void shouldCheckDocumentExistenceIgnoringId() {
        when(jpaRepository.existsByDocumentAndIdNot("123", id)).thenReturn(true);

        assertTrue(repository.existsByDocumentAndIdNot("123", id));
        verify(jpaRepository).existsByDocumentAndIdNot("123", id);
    }

    @Test
    @DisplayName("Deve verificar inexistencia de documento ignorando ID")
    void shouldCheckDocumentNonExistenceIgnoringId() {
        when(jpaRepository.existsByDocumentAndIdNot("123", id)).thenReturn(false);

        assertFalse(repository.existsByDocumentAndIdNot("123", id));
    }

    @Test
    @DisplayName("Deve verificar existencia de email ignorando ID")
    void shouldCheckEmailExistenceIgnoringId() {
        when(jpaRepository.existsByEmailAndIdNot("a@b.com", id)).thenReturn(true);

        assertTrue(repository.existsByEmailAndIdNot("a@b.com", id));
        verify(jpaRepository).existsByEmailAndIdNot("a@b.com", id);
    }

    @Test
    @DisplayName("Deve verificar inexistencia de email ignorando ID")
    void shouldCheckEmailNonExistenceIgnoringId() {
        when(jpaRepository.existsByEmailAndIdNot("a@b.com", id)).thenReturn(false);

        assertFalse(repository.existsByEmailAndIdNot("a@b.com", id));
    }

    @Test
    @DisplayName("Deve buscar todos os clientes paginados")
    void shouldFindAllClientsPaginated() {
        ClientPageQuery query = mock(ClientPageQuery.class);
        ClientFilter filter = mock(ClientFilter.class);
        when(query.filter()).thenReturn(filter);
        when(query.direction()).thenReturn("ASC");
        when(query.sortBy()).thenReturn("name");
        when(query.page()).thenReturn(0);
        when(query.size()).thenReturn(10);

        Page<ClientJpaEntity> page = new PageImpl<>(List.of(entity));

        try (MockedStatic<ClientSpecificationBuilder> specBuilder = mockStatic(ClientSpecificationBuilder.class);
             MockedStatic<ClientJpaMapper> mapper = mockStatic(ClientJpaMapper.class)) {

            Specification<ClientJpaEntity> spec = mock(Specification.class);
            specBuilder.when(() -> ClientSpecificationBuilder.buildFilterSpecification(filter)).thenReturn(spec);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            mapper.when(() -> ClientJpaMapper.toDomain(entity)).thenReturn(domain);

            ClientPageResult result = repository.findAll(query);

            assertEquals(1, result.items().size());
            assertEquals(1, result.totalElements());
            verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }
}
