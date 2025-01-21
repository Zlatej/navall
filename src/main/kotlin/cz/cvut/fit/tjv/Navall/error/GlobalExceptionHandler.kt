package cz.cvut.fit.tjv.Navall.error

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException::class)
    fun handleException(e: ResponseStatusException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            message = e.reason ?: "An unknown error occurred",
            status = e.statusCode.value(),
            error = e.statusCode.toString(),
        )

        if (e.statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error(e) {}
        }
        else {
            log.warn(e) {}
        }
        return ResponseEntity(errorResponse, e.statusCode)
    }
}