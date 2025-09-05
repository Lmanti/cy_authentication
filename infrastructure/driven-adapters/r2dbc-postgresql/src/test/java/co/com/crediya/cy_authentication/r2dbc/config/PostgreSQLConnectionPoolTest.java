package co.com.crediya.cy_authentication.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostgreSQLConnectionPoolTest {

    private PostgreSQLConnectionPool postgreSQLConnectionPool;

    @BeforeEach
    void setUp() {
        postgreSQLConnectionPool = new PostgreSQLConnectionPool();
    }

    @Test
    @DisplayName("Should create connection pool with valid properties")
    void shouldCreateConnectionPoolWithValidProperties() {
        // Given - Crear instancia directa del record
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
            "localhost",
            5432,
            "testdb",
            "public",
            "testuser",
            "testpassword"
        );

        // When
        ConnectionPool connectionPool = postgreSQLConnectionPool.getConnectionConfig(properties);

        // Then
        assertNotNull(connectionPool, "Connection pool should not be null");
    }

    @Test
    @DisplayName("Should create connection pool with different properties")
    void shouldCreateConnectionPoolWithDifferentProperties() {
        // Given
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
            "prod-server",
            5433,
            "production_db",
            "auth_schema",
            "prod_user",
            "secure_password"
        );

        // When
        ConnectionPool connectionPool = postgreSQLConnectionPool.getConnectionConfig(properties);

        // Then
        assertNotNull(connectionPool, "Connection pool should not be null");
    }

    @Test
    @DisplayName("Should create connection pool with default port")
    void shouldCreateConnectionPoolWithDefaultPort() {
        // Given
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
            "localhost",
            PostgreSQLConnectionPool.DEFAULT_PORT,
            "testdb",
            "public",
            "testuser",
            "testpassword"
        );

        // When
        ConnectionPool connectionPool = postgreSQLConnectionPool.getConnectionConfig(properties);

        // Then
        assertNotNull(connectionPool, "Connection pool should not be null");
    }

    @Test
    @DisplayName("Should handle null host gracefully")
    void shouldHandleNullHostGracefully() {
        // Given
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
            null,
            5432,
            "testdb",
            "public",
            "testuser",
            "testpassword"
        );

        // When & Then
        assertThrows(Exception.class, () -> {
            postgreSQLConnectionPool.getConnectionConfig(properties);
        }, "Should throw exception when host is null");
    }

    @Test
    @DisplayName("Should create connection pool even with null database")
    void shouldCreateConnectionPoolEvenWithNullDatabase() {
        // Given
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
            "localhost",
            5432,
            null, // database null
            "public",
            "testuser",
            "testpassword"
        );

        // When & Then - No debería lanzar excepción al crear el pool
        assertDoesNotThrow(() -> {
            ConnectionPool connectionPool = postgreSQLConnectionPool.getConnectionConfig(properties);
            assertNotNull(connectionPool);
            // La excepción se lanzaría al intentar usar la conexión, no al crearla
        });
    }

    @Test
    @DisplayName("Should have correct constant values")
    void shouldHaveCorrectConstantValues() {
        // Then
        assertEquals(12, PostgreSQLConnectionPool.INITIAL_SIZE, "INITIAL_SIZE should be 12");
        assertEquals(15, PostgreSQLConnectionPool.MAX_SIZE, "MAX_SIZE should be 15");
        assertEquals(30, PostgreSQLConnectionPool.MAX_IDLE_TIME, "MAX_IDLE_TIME should be 30");
        assertEquals(5432, PostgreSQLConnectionPool.DEFAULT_PORT, "DEFAULT_PORT should be 5432");
    }

    @Test
    @DisplayName("Should create multiple connection pools independently")
    void shouldCreateMultipleConnectionPoolsIndependently() {
        // Given
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
            "localhost",
            5432,
            "testdb",
            "public",
            "testuser",
            "testpassword"
        );

        // When
        ConnectionPool pool1 = postgreSQLConnectionPool.getConnectionConfig(properties);
        ConnectionPool pool2 = postgreSQLConnectionPool.getConnectionConfig(properties);

        // Then
        assertNotNull(pool1);
        assertNotNull(pool2);
        assertNotSame(pool1, pool2, "Each call should create a new connection pool instance");
    }

    @Test
    @DisplayName("Should verify record properties are accessible")
    void shouldVerifyRecordPropertiesAreAccessible() {
        // Given
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
            "localhost",
            5432,
            "testdb",
            "public",
            "testuser",
            "testpassword"
        );

        // Then - Verificar que el record funciona correctamente
        assertEquals("localhost", properties.host());
        assertEquals(5432, properties.port());
        assertEquals("testdb", properties.database());
        assertEquals("public", properties.schema());
        assertEquals("testuser", properties.username());
        assertEquals("testpassword", properties.password());
    }
}