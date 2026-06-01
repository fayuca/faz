package systems.redtape.faz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Idempotent patches for PostgreSQL volumes created before {@code currency} existed.
 * Hibernate {@code ddl-auto=update} cannot add a NOT NULL column when rows already exist.
 */
@Component
@Profile("prod")
public class PostgresSchemaMigration implements SmartInitializingSingleton {

	private static final Logger log = LoggerFactory.getLogger(PostgresSchemaMigration.class);

	private final JdbcTemplate jdbcTemplate;

	public PostgresSchemaMigration(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterSingletonsInstantiated() {
		migrateCurrencyColumn();
	}

	private void migrateCurrencyColumn() {
		try {
			jdbcTemplate.execute(
					"ALTER TABLE transaction ADD COLUMN IF NOT EXISTS currency VARCHAR(255)");
			int updated = jdbcTemplate.update(
					"UPDATE transaction SET currency = 'USD' WHERE currency IS NULL");
			if (updated > 0) {
				log.info("Backfilled currency for {} existing transaction row(s)", updated);
			}
			try {
				jdbcTemplate.execute(
						"ALTER TABLE transaction ALTER COLUMN currency SET NOT NULL");
			} catch (DataAccessException ex) {
				log.debug("currency NOT NULL constraint already applied");
			}
		} catch (DataAccessException ex) {
			log.warn("Postgres schema patch for currency column failed: {}", ex.getMessage());
		}
	}
}
