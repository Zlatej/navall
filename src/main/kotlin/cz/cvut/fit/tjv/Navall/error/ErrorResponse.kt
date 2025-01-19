package cz.cvut.fit.tjv.Navall.error

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
)