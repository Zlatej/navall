package cz.cvut.fit.tjv.Navall.models.dtos

import cz.cvut.fit.tjv.Navall.models.Transaction.TransactionType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class TransactionDto(
    val id: Long? = null,

    @field:NotNull(message = "Type is mandatory")
    val type: TransactionType,

    @field:NotNull(message = "Amount is mandatory")
    @field:Positive(message = "Amount must be positive")
    val amount: Double,

    @field:NotNull(message = "Paid By Id is mandatory")
    val paidById: Long,

    val createdAt: LocalDateTime? = null,

    val participants: List<TransactionParticipantDto> = emptyList(),
)
