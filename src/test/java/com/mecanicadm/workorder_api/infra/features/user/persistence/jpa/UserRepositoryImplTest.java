package com.mecanicadm.workorder_api.infra.features.user.persistence.jpa;

import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.core.user.domain.port.UserFilter;
import com.mecanicadm.workorder_api.core.user.domain.port.UserPageQuery;
import com.mecanicadm.workorder_api.core.user.domain.port.UserPageResult;
import com.mecanicadm.workorder_api.core.user.exception.UserExceptions;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.UserJpaEntity;
import com.mecanicadm.workorder_api.infra.features.user.persistence.jpa.specification.UserSpecificationBuilder;
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
class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository jpaRepository;

    private UserRepositoryImpl repository;
    private UUID id;
    private User domain;
    private UserJpaEntity entity;

    @BeforeEach
    void setUp() {
        repository = new UserRepositoryImpl(jpaRepository);

        id = UUID.randomUUID();
        domain = mock(User.class);
        lenient().when(domain.getId()).thenReturn(id);

        entity = new UserJpaEntity();
        entity.setId(id);
    }

    @Test
    @DisplayName("Deve criar usuario com sucesso")
    void shouldCreateUserSuccessfully() {
        try (MockedStatic<UserJpaMapper> mapper = mockStatic(UserJpaMapper.class)) {
            mapper.when(() -> UserJpaMapper.toEntity(domain)).thenReturn(entity);
            mapper.when(() -> UserJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            User result = repository.create(domain);

            assertSame(domain, result);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar usuario nulo")
    void shouldThrowExceptionWhenCreatingNullUser() {
        assertThrows(TechnicalException.class, () -> repository.create(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve atualizar usuario com sucesso")
    void shouldUpdateUserSuccessfully() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<UserJpaMapper> mapper = mockStatic(UserJpaMapper.class)) {
            mapper.when(() -> UserJpaMapper.updateEntity(entity, domain)).thenAnswer(invocation -> null);
            mapper.when(() -> UserJpaMapper.toDomain(entity)).thenReturn(domain);
            when(jpaRepository.save(entity)).thenReturn(entity);

            User result = repository.update(domain);

            assertSame(domain, result);
            verify(jpaRepository).findById(id);
            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar usuario nulo")
    void shouldThrowExceptionWhenUpdatingNullUser() {
        assertThrows(TechnicalException.class, () -> repository.update(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar usuario inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFound.class, () -> repository.update(domain));
        verify(jpaRepository).findById(id);
        verify(jpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar usuario por ID com sucesso")
    void shouldFindUserById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<UserJpaMapper> mapper = mockStatic(UserJpaMapper.class)) {
            mapper.when(() -> UserJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<User> result = repository.findById(id);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findById(id);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar usuario por ID inexistente")
    void shouldReturnEmptyWhenUserNotFoundById() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = repository.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve buscar usuario por email com sucesso")
    void shouldFindUserByEmail() {
        String email = "test@test.com";
        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        try (MockedStatic<UserJpaMapper> mapper = mockStatic(UserJpaMapper.class)) {
            mapper.when(() -> UserJpaMapper.toDomain(entity)).thenReturn(domain);

            Optional<User> result = repository.findByEmail(email);

            assertTrue(result.isPresent());
            assertSame(domain, result.get());
            verify(jpaRepository).findByEmail(email);
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar usuario por email inexistente")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        String email = "test@test.com";
        when(jpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = repository.findByEmail(email);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve verificar existencia de email")
    void shouldCheckEmailExistence() {
        when(jpaRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertTrue(repository.existsByEmail("test@test.com"));
        verify(jpaRepository).existsByEmail("test@test.com");
    }

    @Test
    @DisplayName("Deve verificar inexistencia de email")
    void shouldCheckEmailNonExistence() {
        when(jpaRepository.existsByEmail("test@test.com")).thenReturn(false);

        assertFalse(repository.existsByEmail("test@test.com"));
    }

    @Test
    @DisplayName("Deve verificar existencia por ID")
    void shouldCheckExistenceById() {
        when(jpaRepository.existsById(id)).thenReturn(true);

        assertTrue(repository.existsById(id));
        verify(jpaRepository).existsById(id);
    }

    @Test
    @DisplayName("Deve verificar inexistencia por ID")
    void shouldCheckNonExistenceById() {
        when(jpaRepository.existsById(id)).thenReturn(false);

        assertFalse(repository.existsById(id));
    }

    @Test
    @DisplayName("Deve buscar todos os usuarios paginados")
    void shouldFindAllUsersPaginated() {
        UserPageQuery query = mock(UserPageQuery.class);
        UserFilter filter = mock(UserFilter.class);
        when(query.filter()).thenReturn(filter);
        when(query.direction()).thenReturn("ASC");
        when(query.sortBy()).thenReturn("name");
        when(query.page()).thenReturn(0);
        when(query.size()).thenReturn(10);

        Page<UserJpaEntity> page = new PageImpl<>(List.of(entity));

        try (MockedStatic<UserSpecificationBuilder> specBuilder = mockStatic(UserSpecificationBuilder.class);
             MockedStatic<UserJpaMapper> mapper = mockStatic(UserJpaMapper.class)) {

            Specification<UserJpaEntity> spec = mock(Specification.class);
            specBuilder.when(() -> UserSpecificationBuilder.buildFilterSpecification(filter)).thenReturn(spec);
            when(jpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            mapper.when(() -> UserJpaMapper.toDomain(entity)).thenReturn(domain);

            UserPageResult result = repository.findAll(query);

            assertEquals(1, result.items().size());
            assertEquals(1, result.totalElements());
            verify(jpaRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }
}
