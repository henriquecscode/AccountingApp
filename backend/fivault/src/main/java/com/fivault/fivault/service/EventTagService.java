package com.fivault.fivault.service;

import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.EventTag;
import com.fivault.fivault.dto.EventTagDTO;
import com.fivault.fivault.mapper.EventTagMapper;
import com.fivault.fivault.repository.EventTagRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.eventTag.EventTagCreateResult;
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


    public Output<EventTagCreateResult> create(Long domainId, String name, String description, UUID parentEventTagId) {

        if(name == null || name.isBlank()){
            return Output.failure(ErrorCode.EVENT_CREATE_NO_NAME);
        }

        Domain domain = entityManager.getReference(Domain.class, domainId);
        Optional<EventTag> parentEventTagOptional = eventTagRepository.findByEventTagId(parentEventTagId);
        EventTag parentEventTag = parentEventTagOptional.orElse(null);
        EventTag eventTag = new EventTag();
        eventTag.setDomain(domain);
        eventTag.setName(name);
        eventTag.setDescription(description);
        eventTag.setParentEventTag(parentEventTag);
        eventTagRepository.save(eventTag);
        EventTagDTO eventTagDTO = new EventTagDTO(eventTag.getEventTagId(), name, description, parentEventTagId);
        return Output.success(new EventTagCreateResult(eventTagDTO));
    }

    public Output<EventTagListResult> list(Long domainId) {
        List<EventTag> eventTagList = eventTagRepository.findAllTagsRecursive(domainId);
        List<EventTagDTO> eventTagDTOList = eventTagList.stream().map(
                eventTagMapper::toDTO
        ).toList();
        return Output.success(new EventTagListResult(eventTagDTOList));
    }
}
