package cz.cvut.fit.tjv.Navall.repository

import cz.cvut.fit.tjv.Navall.models.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepo: JpaRepository<Transaction, Long> {
}