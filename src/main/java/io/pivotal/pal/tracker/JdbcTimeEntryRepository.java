package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.util.List;

import static java.util.Collections.*;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (:projectId, :userId, :date, :hours)",
                new BeanPropertySqlParameterSource(timeEntry),
                keyHolder);

        return new TimeEntry(
                keyHolder.getKey().longValue(),
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours());
    }

    @Override
    public TimeEntry find(long id) {
        List<TimeEntry> timeEntries = jdbcTemplate.query(
                "SELECT * FROM time_entries WHERE id = :id",
                singletonMap("id", id),
                timeEntryRowMapper()
        );
        return timeEntries.size() == 0 ? null : timeEntries.get(0);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query(
                "SELECT * FROM time_entries",
                timeEntryRowMapper()
        );
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(
                "DELETE FROM time_entries WHERE id = :id",
                singletonMap("id", id)
        );
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry newTimeEntry = new TimeEntry(
                id,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours());
        jdbcTemplate.update(
                "UPDATE time_entries SET project_id = :projectId, user_id = :userId, date = :date, hours = :hours WHERE id = :id",
                new BeanPropertySqlParameterSource(newTimeEntry));
        return newTimeEntry;
    }

    private RowMapper<TimeEntry> timeEntryRowMapper() {
        return (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
        );
    }

}
