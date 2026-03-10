package telegram.entity

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity("users")
data class UserSession(

    @field:Id
    val id: Long,

    val state: String? = null,
    val phone: String? = null,
    val projectName: String? = null
)