package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
