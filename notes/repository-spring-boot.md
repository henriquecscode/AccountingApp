// CREATE
User user = new User();
repository.save(user);                    // INSERT or UPDATE
repository.saveAll(List.of(user1, user2)); // Batch insert

// READ
repository.findById(1L);                  // SELECT by ID
repository.findAll();                     // SELECT all
repository.findAllById(List.of(1L, 2L));  // SELECT multiple by IDs
repository.count();                       // COUNT(*)
repository.existsById(1L);                // Check existence

// UPDATE
user.setEmail("new@email.com");
repository.save(user);                    // UPDATE

// DELETE
repository.delete(user);                  // DELETE
repository.deleteById(1L);                // DELETE by ID
repository.deleteAll();                   // DELETE all (dangerous!)

findBy...           // SELECT
existsBy...         // EXISTS
countBy...          // COUNT
deleteBy...         // DELETE

// Logical operators
findByEmailAndIsActive(email, true)          // WHERE email = ? AND is_active = ?
findByEmailOrUsername(email, username)       // WHERE email = ? OR username = ?

// Comparison
findByAgeGreaterThan(18)                     // WHERE age > ?
findByAgeLessThan(65)                        // WHERE age < ?
findByAgeGreaterThanEqual(18)                // WHERE age >= ?
findByAgeBetween(18, 65)                     // WHERE age BETWEEN ? AND ?

// String operations
findByEmailContaining("gmail")                // WHERE email LIKE %?%
findByEmailStartingWith("admin")              // WHERE email LIKE ?%
findByEmailEndingWith("@company.com")         // WHERE email LIKE %?

// Null checks
findByLastLoginAtIsNull()                     // WHERE last_login_at IS NULL
findByLastLoginAtIsNotNull()                  // WHERE last_login_at IS NOT NULL

// Ordering
findByIsActiveTrueOrderByCreatedAtDesc()     // ORDER BY created_at DESC

// Limiting
findTop10ByIsActiveTrue()                    // LIMIT 10
findFirst5ByOrderByCreatedAtDesc()           // LIMIT 5

# Lazy loading for index use

3. How to fetch only specific columns

If you want only a subset of columns (to avoid reading large fields like passwordHash), you have a few options:

a) Use a DTO / projection
public record UserEmailOnly(String email, String isActive) {}

@Query("SELECT new com.fivault.fivault.dto.UserEmailOnly(u.email, u.isActive) FROM AppUser u WHERE u.email = :email")
UserEmailOnly findEmailOnly(@Param("email") String email);


This generates a SQL query like:

SELECT email, is_active FROM app_users WHERE email = ?


Hibernate returns only the requested fields in the DTO.

No entity proxy is created.

This works perfectly for index-only scans if the DB supports it.

b) Use interface-based projections (Spring Data JPA)
public interface EmailOnly {
    String getEmail();
    Boolean getIsActive();
}

EmailOnly findByEmail(String email);


Spring Data will generate a query that selects only the required columns.

Useful if you want a simple interface instead of a DTO class.

c) Use native query
@Query(value = "SELECT email, is_active FROM app_users WHERE email = :email", nativeQuery = true)
Object[] findEmailOnlyNative(@Param("email") String email);


Full control over columns selected.

Good for index-only queries in Postgres or MySQL.

d) Lazy-loading large fields (less common)

If you have a large column like passwordHash:

@Basic(fetch = FetchType.LAZY)
@Column(nullable = false)
private String passwordHash;


Hibernate may lazily load this field as a proxy if bytecode enhancement is enabled.

Caveat: this requires build-time bytecode instrumentation (or Hibernate 6+ runtime enhancement) — it’s not automatic.

Basic types like String are not lazy by default.

So for a simple string column, DTO/projection is usually simpler than lazy-loading individual fields.