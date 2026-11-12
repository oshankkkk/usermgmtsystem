package com.example.demo.repository;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Low-level access with Spring JDBC (JdbcTemplate): plain SQL for the dashboard numbers. */
@Repository
public class UserStatsJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserStatsJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Keys: total, active_count, admin_count, manager_count, user_count. */
    public Map<String, Object> getStats() {
        String sql = """
                SELECT COUNT(*) AS total,
                       COALESCE(SUM(CASE WHEN active = 1 THEN 1 ELSE 0 END), 0) AS active_count,
                       COALESCE(SUM(CASE WHEN [role] = 'ADMIN' THEN 1 ELSE 0 END), 0) AS admin_count,
                       COALESCE(SUM(CASE WHEN [role] = 'MANAGER' THEN 1 ELSE 0 END), 0) AS manager_count,
                       COALESCE(SUM(CASE WHEN [role] = 'USER' THEN 1 ELSE 0 END), 0) AS user_count
                FROM users
                """;
        return jdbcTemplate.queryForMap(sql);
    }
}
