package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.TransactionParticipant
import cz.cvut.fit.tjv.Navall.repository.TransactionParticipantRepo
import org.springframework.stereotype.Service

@Service
class TransactionParticipantService(
    private val transactionParticipantRepo: TransactionParticipantRepo
) {
    fun saveTransactionParticipant(transactionParticipant: TransactionParticipant) {
        transactionParticipantRepo.save(transactionParticipant)
    }
}