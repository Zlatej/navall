package cz.cvut.fit.tjv.Navall.repository

import cz.cvut.fit.tjv.Navall.models.TransactionParticipant
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionParticipantRepo: JpaRepository<TransactionParticipant, Long> {
}