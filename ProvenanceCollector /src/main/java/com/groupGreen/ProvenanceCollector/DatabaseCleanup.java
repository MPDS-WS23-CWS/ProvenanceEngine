package com.groupGreen.ProvenanceCollector;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
public class DatabaseCleanup {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleanup(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    @PreDestroy
    public void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM tasks");
        jdbcTemplate.execute("DELETE FROM workflows");
        jdbcTemplate.execute("DELETE FROM resources");
    }
}
