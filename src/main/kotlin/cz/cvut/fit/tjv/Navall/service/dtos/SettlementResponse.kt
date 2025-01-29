package cz.cvut.fit.tjv.Navall.service.dtos

import cz.cvut.fit.tjv.Navall.models.Transaction

data class SettlementResponse(
    val type: Transaction.TransactionType = Transaction.TransactionType.SETTLEMENT,
    val amount: Double,
    val paidById: Long,
    val participants: List<TransactionParticipantDto>
)
