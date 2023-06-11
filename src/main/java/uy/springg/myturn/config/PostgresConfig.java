package uy.springg.myturn.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

import static uy.springg.myturn.config.GCPConstants.SECRET_MYTURN_POSTGRESQL_PASSWORD;
import static uy.springg.myturn.config.GCPConstants.SECRET_MYTURN_POSTGRESQL_USERNAME;
import static uy.springg.myturn.config.GCPConstants.SECRET_MYTURN_POSTGRESQL_URL;

@CommonsLog
@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
public class PostgresConfig {

    private final Map<String, String> secrets;

    /* Postgres database configuration
     *
     * HikariPool-1 - default configuration:
     * allowPoolSuspension.............false
     * autoCommit......................true
     * catalog.........................none
     * connectionInitSql...............none
     * connectionTestQuery.............none
     * connectionTimeout...............30000
     * dataSource......................none
     * dataSourceClassName.............none
     * dataSourceJNDI..................none
     * dataSourceProperties............{password=<masked>}
     * driverClassName................."org.postgresql.Driver"
     * exceptionOverrideClassName......none
     * healthCheckProperties...........{}
     * healthCheckRegistry.............none
     * idleTimeout.....................600000
     * initializationFailTimeout.......1
     * isolateInternalQueries..........false
     * jdbcUrl.........................{url=<masked>}
     * keepaliveTime...................0
     * leakDetectionThreshold..........0
     * maxLifetime.....................1800000
     * maximumPoolSize.................10
     * metricRegistry..................none
     * metricsTrackerFactory...........none
     * minimumIdle.....................10
     * password........................<masked>
     * poolName........................"HikariPool-1"
     * readOnly........................false
     * registerMbeans..................false
     * scheduledExecutor...............none
     * schema..........................none
     * threadFactory...................internal
     * transactionIsolation............default
     * username........................{username=<masked>}
     * validationTimeout...............5000
     */
    @Bean
    public DataSource dataSource() {
        log.info("Configuring Postgres datasource...");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(secrets.get(SECRET_MYTURN_POSTGRESQL_URL));
        dataSource.setUsername(secrets.get(SECRET_MYTURN_POSTGRESQL_USERNAME));
        dataSource.setPassword(secrets.get(SECRET_MYTURN_POSTGRESQL_PASSWORD));
        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;
    }

}
