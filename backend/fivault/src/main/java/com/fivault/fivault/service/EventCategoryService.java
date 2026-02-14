package com.fivault.fivault.service;

import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.EventCategory;
import com.fivault.fivault.dto.EventCategoryDTO;
import com.fivault.fivault.mapper.EventCategoryMapper;
import com.fivault.fivault.repository.EventCategoryRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.result.eventCategory.EventCategoryCreateUpdateResult;
import com.fivault.fivault.service.result.eventCategory.EventCategoryListResult;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventCategoryService {
    private final EntityManager entityManager;

    public final EventCategoryRepository eventCategoryRepository;
    public final EventCategoryMapper eventCategoryMapper;

    public EventCategoryService(EntityManager entityManager, EventCategoryRepository eventCategoryRepository, EventCategoryMapper eventCategoryMapper) {
        this.entityManager = entityManager;
        this.eventCategoryRepository = eventCategoryRepository;
        this.eventCategoryMapper = eventCategoryMapper;
    }


    public Output<EventCategoryCreateUpdateResult> create(Long domainId, String name, String description, UUID parentEventCategoryId) {

        if (name == null || name.isBlank()) {
            return Output.failure(ErrorCode.EVENTTAG_CREATE_NO_NAME);
        }

        Domain domain = entityManager.getReference(Domain.class, domainId);
        EventCategory parentEventCategory = parentEventCategoryId != null ? entityManager.getReference(EventCategory.class, parentEventCategoryId) : null;
        EventCategory eventCategory = new EventCategory();
        eventCategory.setDomain(domain);
        eventCategory.setName(name);
        eventCategory.setDescription(description);
        eventCategory.setParentEventCategory(parentEventCategory);
        eventCategoryRepository.save(eventCategory);
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDTO(eventCategory);
        return Output.success(new EventCategoryCreateUpdateResult(eventCategoryDTO));
    }

    public Output<EventCategoryCreateUpdateResult> update(Long domainId, UUID eventCategoryId, String name, String description, UUID parentEventCategoryId) {

        if (name == null || name.isBlank()) {
            return Output.failure(ErrorCode.EVENTTAG_CREATE_NO_NAME);
        }

        Optional<EventCategory> eventCategoryOptional = eventCategoryRepository.findByEventCategoryId(eventCategoryId);
        if (eventCategoryOptional.isEmpty()) {
            return Output.failure(ErrorCode.EVENTTAG_FIND_BY_ID_ERROR);
        }
        EventCategory eventCategory = eventCategoryOptional.get();
        Domain domain = entityManager.getReference(Domain.class, domainId);
        EventCategory parentEventCategory = parentEventCategoryId != null ? entityManager.getReference(EventCategory.class, parentEventCategoryId) : null;
        eventCategory.setDomain(domain);
        eventCategory.setName(name);
        eventCategory.setDescription(description);
        eventCategory.setParentEventCategory(parentEventCategory);
        eventCategoryRepository.save(eventCategory);
        EventCategoryDTO eventCategoryDTO = eventCategoryMapper.toDTO(eventCategory);
        return Output.success(new EventCategoryCreateUpdateResult(eventCategoryDTO));
    }

    public Output<EventCategoryListResult> list(Long domainId) {
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllCategoriesRecursive(domainId);
        List<EventCategoryDTO> eventCategoryDTOList = eventCategoryList.stream().map(
                eventCategoryMapper::toDTO
        ).toList();
        return Output.success(new EventCategoryListResult(eventCategoryDTOList));
    }

    public Output<Void> delete(UUID eventCategoryId) {
        List<EventCategory> eventCategoryList = eventCategoryRepository.findAllCategoriesRecursiveByEventCategoryId(eventCategoryId);
        if (eventCategoryList.size() > 1) {
            return Output.failure(ErrorCode.EVENTTAG_DELETE_TAG_NOT_LEAF_ERROR);
        }

        EventCategory eventCategory = eventCategoryList.getFirst();
        eventCategory.setRemoved(true);
        eventCategoryRepository.save(eventCategory);

        return Output.success(null);
    }
}
