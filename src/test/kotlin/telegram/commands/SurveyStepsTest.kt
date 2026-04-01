package telegram.commands

/**
 * РўРµСЃС‚С‹ РѕРїРёСЃР°РЅРёСЏ С€Р°РіРѕРІ РѕРїСЂРѕСЃР°.
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import telegram.enums.UserStates
import telegram.persistence.UserSession

class SurveyStepsTest {

    @Test
    fun `previous step for completed should be purpose step`() {
        val step = previousStepFor(UserStates.COMPLETED)

        assertNotNull(step)
        assertEquals(UserStates.WAITING_FOR_PURPOSE, step!!.state)
        assertEquals("РЅР°Р·РЅР°С‡РµРЅРёРµ", step.stepName)
    }

    @Test
    fun `previous step for first step should be null`() {
        assertNull(previousStepFor(UserStates.WAITING_FOR_PHONE))
    }

    @Test
    fun `clear on return should clear target field only`() {
        val session = UserSession(
            chatId = 1L,
            state = UserStates.WAITING_FOR_PURPOSE,
            phone = "88888888888",
            projectName = "Project",
            purpose = "Purpose",
        )

        val step = previousStepFor(UserStates.COMPLETED)!!
        val cleared = step.clearOnReturn(session)

        assertEquals("88888888888", cleared.phone)
        assertEquals("Project", cleared.projectName)
        assertEquals(null, cleared.purpose)
    }
}


