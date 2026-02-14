package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventCategoryRepository extends JpaRepository<EventCategory, UUID> {
    @Query(value = """
            WITH RECURSIVE category_tree AS (
                SELECT *
                FROM domain_event_categories
                WHERE domain_id = :domainId
                  AND parent_event_category_id IS NULL
                  AND is_removed = false
            
                UNION ALL
            
                SELECT child.*
                FROM domain_event_categories child
                JOIN category_tree parent
                  ON child.parent_event_category_id = parent.event_category_id
                WHERE child.is_removed = false
            )
            SELECT * FROM category_tree
            """, nativeQuery = true)
    List<EventCategory> findAllCategoriesRecursive(Long domainId);

    @Query(value = """
            WITH RECURSIVE category_tree AS (
                SELECT *
                FROM domain_event_categories
                WHERE event_category_id = :eventCategoryId
                  AND is_removed = false
            
                UNION ALL
            
                SELECT child.*
                FROM domain_event_categories child
                JOIN category_tree parent
                  ON child.parent_event_category_id = parent.event_category_id
                WHERE child.is_removed = false
            )
            SELECT * FROM category_tree
            """, nativeQuery = true)
    List<EventCategory> findAllCategoriesRecursiveByEventCategoryId(UUID eventCategoryId);

    Optional<EventCategory> findByEventCategoryId(UUID eventCategoryId);
}
