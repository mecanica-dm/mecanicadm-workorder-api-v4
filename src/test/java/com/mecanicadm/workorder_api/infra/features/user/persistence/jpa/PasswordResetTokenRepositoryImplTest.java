package com.mecanicadm.workorder_api.infra.features.user.persistence.jpa;

import com.mecanicadm.workorder_api.core.user.domain.PasswordResetToken;
import com.mecanicadm.workorder_api.core.user.domain.User;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.PasswordResetTokenJpaEntity;
import com.mecanicadm.workorder_api.infra.features.user.persistence.entity.UserJpaEntity;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenRepositoryImplTest {

    @Mock
    private PasswordResetTokenJpaRepository jpaRepository;

    private PasswordResetTokenRepositoryImpl repository;
    private PasswordResetToken token;
    private PasswordResetTokenJpaEntity entity;
    private User user;
    private UserJpaEntity userEntity;

    @BeforeEach
    void setUp() {
        repository = new PasswordResetTokenRepositoryImpl(jpaRepository);

        token = mock(PasswordResetToken.class);
        user = mock(User.class);
        entity = new PasswordResetTokenJpaEntity();
        userEntity = new UserJpaEntity();
    }

    @Test
    @DisplayName("Deve deletar tokens por usuario com sucesso")
    void shouldDeleteByUserSuccessfully() {
        try (MockedStatic<UserJpaMapper> userMapper = mockStatic(UserJpaMapper.class)) {
            userMapper.when(() -> UserJpaMapper.toEntity(user)).thenReturn(userEntity);

            repository.deleteByUser(user);

            verify(jpaRepository).deleteByUser(userEntity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao deletar tokens por usuario nulo")
    void shouldThrowExceptionWhenDeletingByNullUser() {
        assertThrows(TechnicalException.class, () -> repository.deleteByUser(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve salvar token com sucesso")
    void shouldSaveTokenSuccessfully() {
        try (MockedStatic<PasswordResetTokenJpaMapper> mapper = mockStatic(PasswordResetTokenJpaMapper.class)) {
            mapper.when(() -> PasswordResetTokenJpaMapper.toEntity(token)).thenReturn(entity);

            repository.save(token);

            verify(jpaRepository).save(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao salvar token nulo")
    void shouldThrowExceptionWhenSavingNullToken() {
        assertThrows(TechnicalException.class, () -> repository.save(null));
        verifyNoInteractions(jpaRepository);
    }

    @Test
    @DisplayName("Deve buscar token por codigo com sucesso")
    void shouldFindByTokenSuccessfully() {
        when(jpaRepository.findByToken("abc123")).thenReturn(Optional.of(entity));

        try (MockedStatic<PasswordResetTokenJpaMapper> mapper = mockStatic(PasswordResetTokenJpaMapper.class)) {
            mapper.when(() -> PasswordResetTokenJpaMapper.toDomain(entity)).thenReturn(token);

            Optional<PasswordResetToken> result = repository.findByToken("abc123");

            assertTrue(result.isPresent());
            assertSame(token, result.get());
            verify(jpaRepository).findByToken("abc123");
        }
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar token inexistente")
    void shouldReturnEmptyWhenTokenNotFound() {
        when(jpaRepository.findByToken("abc123")).thenReturn(Optional.empty());

        Optional<PasswordResetToken> result = repository.findByToken("abc123");

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByToken("abc123");
    }

    @Test
    @DisplayName("Deve deletar token com sucesso")
    void shouldDeleteTokenSuccessfully() {
        try (MockedStatic<PasswordResetTokenJpaMapper> mapper = mockStatic(PasswordResetTokenJpaMapper.class)) {
            mapper.when(() -> PasswordResetTokenJpaMapper.toEntity(token)).thenReturn(entity);

            repository.delete(token);

            verify(jpaRepository).delete(entity);
        }
    }

    @Test
    @DisplayName("Deve lancar excecao ao deletar token nulo")
    void shouldThrowExceptionWhenDeletingNullToken() {
        assertThrows(TechnicalException.class, () -> repository.delete(null));
        verifyNoInteractions(jpaRepository);
    }
}
