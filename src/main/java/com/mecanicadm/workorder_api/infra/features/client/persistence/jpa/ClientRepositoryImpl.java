package com.mecanicadm.workorder_api.infra.features.client.persistence.jpa;

import com.mecanicadm.workorder_api.core.client.domain.Client;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientGateway;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageQuery;
import com.mecanicadm.workorder_api.core.client.domain.port.ClientPageResult;
import com.mecanicadm.workorder_api.infra.features.client.persistence.entity.ClientJpaEntity;
import com.mecanicadm.workorder_api.infra.features.client.persistence.jpa.specification.ClientSpecificationBuilder;
import com.mecanicadm.workorder_api.shared.exception.TechnicalException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Repository
public class ClientRepositoryImpl implements ClientGateway {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryImpl(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client create(Client client) {
        if (isNull(client)) {
            throw new TechnicalException("error.technical.entity.null", "Client", "criação");
        }
        ClientJpaEntity entity = ClientJpaMapper.toEntity(client);
        ClientJpaEntity saved = jpaRepository.save(entity);
        return ClientJpaMapper.toDomain(saved);
    }

    @Override
    public Client update(Client client) {
        if (isNull(client)) {
            throw new TechnicalException("error.technical.entity.null", "Client", "atualização");
        }
        ClientJpaEntity entity = ClientJpaMapper.toEntity(client);
        ClientJpaEntity saved = jpaRepository.save(entity);
        return ClientJpaMapper.toDomain(saved);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return jpaRepository.findById(id).map(ClientJpaMapper::toDomain);
    }

    @Override
    public Optional<Client> findByDocument(String document) {
        return jpaRepository.findByDocument(document).map(ClientJpaMapper::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByDocumentAndIdNot(String document, UUID id) {
        return jpaRepository.existsByDocumentAndIdNot(document, id);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return jpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public ClientPageResult findAll(ClientPageQuery query) {
        Specification<ClientJpaEntity> spec = ClientSpecificationBuilder.buildFilterSpecification(query.filter());
        Sort sort = Sort.by(Sort.Direction.fromString(query.direction()), query.sortBy());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

        var page = jpaRepository.findAll(spec, pageable);
        return new ClientPageResult(page.map(ClientJpaMapper::toDomain).getContent(), page.getTotalElements());
    }
}
