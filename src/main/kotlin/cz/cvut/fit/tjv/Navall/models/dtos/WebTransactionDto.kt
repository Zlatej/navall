package cz.cvut.fit.tjv.Navall.models.dtos

import cz.cvut.fit.tjv.Navall.models.Transaction.TransactionType
import java.time.LocalDateTime

data class WebTransactionDto(
    val id: Long? = null,

    val type: TransactionType,

    val amount: Double,

    val paidById: Long? = null,
    val paidByName: String,

    val createdAt: LocalDateTime? = null,

    val participants: List<TransactionParticipantDto> = emptyList(),
    val participantNames: List<String> = emptyList(),
)



