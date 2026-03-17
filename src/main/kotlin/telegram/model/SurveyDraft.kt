package telegram.model

/**
 * Черновик анкеты в памяти: хранит ответы пользователя до сохранения.
 */

data class SurveyDraft(
    val phone: String? = null,
    val projectName: String? = null,
    val purpose: String? = null,
)