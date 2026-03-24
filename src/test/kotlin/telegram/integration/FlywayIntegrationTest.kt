package telegram.integration

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.sql.DataSource

@MicronautTest(environments = ["test"])
class FlywayIntegrationTest {

    @Inject
    lateinit var dataSource: DataSource

    @Test
    fun `flyway should create user sessions and processed updates tables`() {
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(
                    """
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_name IN ('user_sessions', 'processed_updates')
                    """.trimIndent()
                ).use { resultSet ->
                    val tables = mutableSetOf<String>()
                    while (resultSet.next()) {
                        tables += resultSet.getString("table_name")
                    }

                    assertTrue(tables.contains("user_sessions"))
                    assertTrue(tables.contains("processed_updates"))
                }
            }
        }
    }
}
