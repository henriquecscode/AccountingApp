# Time series
https://stackoverflow.com/questions/60279898/how-does-calendar-provider-store-instances-of-infinitely-repeating-events
https://icalendar.org/iCalendar-RFC-5545/3-8-5-3-recurrence-rule.html

# VCS
My Recommendation
Use Flyway for database migrations. It's the industry standard for tracking database schema changes:

Add Flyway dependency to pom.xml:

xml<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

Create migration file: V2__remove_salt_column.sql
Spring Boot will automatically run it on startup

