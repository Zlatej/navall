package cz.cvut.fit.tjv.Navall.service.dtos

import cz.cvut.fit.tjv.Navall.models.Transaction.TransactionType
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class TransactionDto(
    val id: Long? = null,
    @field:NotBlank(message = "Type cannot be blank")
    val type: TransactionType,
    @field:NotBlank(message = "Amount cannot be blank")
    val amount: Double,
    @field:NotBlank(message = "Paid By Id cannot be blank")
    val paidById: Long,
    val createdAt: LocalDateTime? = null,
    val participants: List<TransactionParticipantDto> = emptyList(),
)
