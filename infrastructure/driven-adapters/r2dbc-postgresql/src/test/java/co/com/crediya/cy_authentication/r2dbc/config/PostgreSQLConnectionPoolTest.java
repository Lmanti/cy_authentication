package co.com.crediya.cy_authentication.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgreSQLConnectionPoolTest {

    @Mock(strictness = Strictness.LENIENT)
    private PostgresqlConnectionProperties properties;

    @InjectMocks
    private PostgreSQLConnectionPool postgreSQLConnectionPool;

    @Test
    void getConnectionConfig_shouldCreateConnectionPool() {
        // Arrange
        when(properties.host()).thenReturn("localhost");
        when(properties.port()).thenReturn(5432);
        when(properties.database()).thenReturn("testdb");
        when(properties.schema()).thenReturn("public");
        when(properties.username()).thenReturn("testuser");
        when(properties.password()).thenReturn("testpassword");

        // Act
        ConnectionPool connectionPool = postgreSQLConnectionPool.getConnectionConfig(properties);

        // Assert
        assertNotNull(connectionPool, "Connection pool should not be null");
        
        // Verificar que se llamaron los métodos correctos
        verify(properties).host();
        verify(properties).port();
        verify(properties).database();
        verify(properties).schema();
        verify(properties).username();
        verify(properties).password();
    }

    @Test
    void constants_shouldHaveCorrectValues() {
        // Assert
        assertEquals(12, PostgreSQLConnectionPool.INITIAL_SIZE, "INITIAL_SIZE should be 12");
        assertEquals(15, PostgreSQLConnectionPool.MAX_SIZE, "MAX_SIZE should be 15");
        assertEquals(30, PostgreSQLConnectionPool.MAX_IDLE_TIME, "MAX_IDLE_TIME should be 30");
        assertEquals(5432, PostgreSQLConnectionPool.DEFAULT_PORT, "DEFAULT_PORT should be 5432");
    }
}