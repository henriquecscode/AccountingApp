package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.EventTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventTagRepository extends JpaRepository<EventTag, UUID> {
    @Query(value = """
            WITH RECURSIVE tag_tree AS (
                SELECT *
                FROM domain_event_tags
                WHERE domain_id = :domainId
                  AND parent_event_tag_id IS NULL
            
                UNION ALL
            
                SELECT child.*
                FROM domain_event_tags child
                JOIN tag_tree parent
                  ON child.parent_event_tag_id = parent.event_tag_id
            )
            SELECT * FROM tag_tree
            """, nativeQuery = true)
    List<EventTag> findAllTagsRecursive(Long domainId);

    Optional<EventTag> findByEventTagId(UUID eventTagId);
}
