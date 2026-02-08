package com.fivault.fivault.service;

import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.Event;
import com.fivault.fivault.database.model.EventTag;
import com.fivault.fivault.dto.EventTagDTO;
import com.fivault.fivault.mapper.EventTagMapper;
import com.fivault.fivault.repository.EventTagRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.eventTag.EventTagCreateUpdateResult;
import com.fivault.fivault.service.result.eventTag.EventTagListResult;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventTagService {
    private final EntityManager entityManager;

    public final EventTagRepository eventTagRepository;
    public final EventTagMapper eventTagMapper;

    public EventTagService(EntityManager entityManager, EventTagRepository eventTagRepository, EventTagMapper eventTagMapper) {
        this.entityManager = entityManager;
        this.eventTagRepository = eventTagRepository;
        this.eventTagMapper = eventTagMapper;
    }


    public Output<EventTagCreateUpdateResult> create(Long domainId, String name, String description, UUID parentEventTagId) {

        if (name == null || name.isBlank()) {
            return Output.failure(ErrorCode.EVENTTAG_CREATE_NO_NAME);
        }

        Domain domain = entityManager.getReference(Domain.class, domainId);
        EventTag parentEventTag = parentEventTagId != null ? entityManager.getReference(EventTag.class, parentEventTagId) : null;
        EventTag eventTag = new EventTag();
        eventTag.setDomain(domain);
        eventTag.setName(name);
        eventTag.setDescription(description);
        eventTag.setParentEventTag(parentEventTag);
        eventTagRepository.save(eventTag);
        EventTagDTO eventTagDTO = eventTagMapper.toDTO(eventTag);
        return Output.success(new EventTagCreateUpdateResult(eventTagDTO));
    }

    public Output<EventTagCreateUpdateResult> update(Long domainId, UUID eventTagId, String name, String description, UUID parentEventTagId) {

        if (name == null || name.isBlank()) {
            return Output.failure(ErrorCode.EVENTTAG_CREATE_NO_NAME);
        }

        Optional<EventTag> eventTagOptional = eventTagRepository.findByEventTagId(eventTagId);
        if (eventTagOptional.isEmpty()) {
            return Output.failure(ErrorCode.EVENTTAG_FIND_BY_ID_ERROR);
        }
        EventTag eventTag = eventTagOptional.get();
        Domain domain = entityManager.getReference(Domain.class, domainId);
        EventTag parentEventTag = parentEventTagId != null ? entityManager.getReference(EventTag.class, parentEventTagId) : null;
        eventTag.setDomain(domain);
        eventTag.setName(name);
        eventTag.setDescription(description);
        eventTag.setParentEventTag(parentEventTag);
        eventTagRepository.save(eventTag);
        EventTagDTO eventTagDTO = eventTagMapper.toDTO(eventTag);
        return Output.success(new EventTagCreateUpdateResult(eventTagDTO));
    }

    public Output<EventTagListResult> list(Long domainId) {
        List<EventTag> eventTagList = eventTagRepository.findAllTagsRecursive(domainId);
        List<EventTagDTO> eventTagDTOList = eventTagList.stream().map(
                eventTagMapper::toDTO
        ).toList();
        return Output.success(new EventTagListResult(eventTagDTOList));
    }

    public Output<Void> delete(UUID eventTagId) {
        List<EventTag> eventTagList = eventTagRepository.findAllTagsRecursiveByEventTagId(eventTagId);
        if (eventTagList.size() > 1) {
            return Output.failure(ErrorCode.EVENTTAG_DELETE_TAG_NOT_LEAF_ERROR);
        }

        EventTag eventTag = eventTagList.getFirst();
        eventTag.setRemoved(true);
        eventTagRepository.save(eventTag);

        return Output.success(null);
    }
}
