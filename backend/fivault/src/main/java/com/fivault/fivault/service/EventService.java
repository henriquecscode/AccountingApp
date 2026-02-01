package com.fivault.fivault.service;

import com.fivault.fivault.database.model.Domain;
import com.fivault.fivault.database.model.Event;
import com.fivault.fivault.repository.EventRepository;
import com.fivault.fivault.service.exception.ErrorCode;
import com.fivault.fivault.service.params.event.EventCreateParams;
import com.fivault.fivault.service.result.event.EventCreateResult;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.EntityReference;

@Service
public class EventService {

    private final EntityManager entityManager;
    private final EventRepository eventRepository;

    public EventService(EntityManager entityManager, EventRepository eventRepository) {
        this.entityManager = entityManager;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Output<EventCreateResult> createEvent(EventCreateParams params) {

        if(params.title().isBlank()){
            return Output.failure(ErrorCode.EVENT_CREATE_NO_TITLE);
        }
        if(params.startTimestamp() == null){
            return Output.failure(ErrorCode.EVENT_CREATE_NO_START_TIMESTAMP);
        }
        if(params.endTimestamp() != null && params.startTimestamp().isAfter(params.endTimestamp())){
            return Output.failure(ErrorCode.EVENT_CREATE_INVALID_END_TIMESTAMP);
        }
        Domain domain = entityManager.getReference(Domain.class, params.domainId());

        Event event = new Event();
        event.setTitle(params.title());
        event.setDescription(params.description());
        event.setDomain(domain);
        event.setStartTimestamp(params.startTimestamp());
        event.setEndTimestamp(params.endTimestamp());
        eventRepository.save(event);

        return Output.success(new EventCreateResult(event.getEventId()));
    }
}
