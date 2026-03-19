package telegram.persistence

/**
 * Инициализация схемы: создает таблицу survey_submissions при старте приложения.
 */

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.data.connection.annotation.Connectable
import io.micronaut.runtime.event.ApplicationStartupEvent
import jakarta.inject.Singleton
import javax.sql.DataSource

@Singleton
open class SurveySubmissionSchemaInitializer(
    private val dataSource: DataSource,
) : ApplicationEventListener<ApplicationStartupEvent> {

    @Connectable // Гарантируем "текущее подключение" для контекстных подключений Micronaut Data.
    override fun onApplicationEvent(event: ApplicationStartupEvent) {
        dataSource.connection.use { conn ->
            conn.createStatement().use { st ->
                st.execute(
                    """
                    CREATE TABLE IF NOT EXISTS survey_submissions (
                        id BIGSERIAL PRIMARY KEY,
                        chat_id BIGINT NOT NULL,
                        phone VARCHAR(32) NOT NULL,
                        project_name VARCHAR(255) NOT NULL,
                        purpose TEXT NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL
                    );
                    """.trimIndent()
                )

                st.execute(
                    """
                    CREATE TABLE IF NOT EXISTS user_sessions (
                        chat_id BIGINT PRIMARY KEY,
                        state VARCHAR(32),
                        phone VARCHAR(32),
                        project_name VARCHAR(255),
                        purpose TEXT,
                        updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
                    );
                    """.trimIndent()
                )
            }
        }
    }
}
