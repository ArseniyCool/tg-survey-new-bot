package telegram.persistence

import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.event.ApplicationStartupEvent
import jakarta.inject.Singleton
import javax.sql.DataSource

@Singleton
@Requires(env = ["db"])
class SurveySubmissionSchemaInitializer(
    private val dataSource: DataSource,
) : ApplicationEventListener<ApplicationStartupEvent> {

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
            }
        }
    }
}