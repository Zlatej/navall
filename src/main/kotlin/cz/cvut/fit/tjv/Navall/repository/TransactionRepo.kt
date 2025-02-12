package cz.cvut.fit.tjv.Navall.repository

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepo : JpaRepository<Transaction, Long> {
    fun findTransactionById(id: Long): Transaction?
    fun group(group: Group): MutableList<Transaction>
    fun getTransactionsByGroup_Id(groupId: Long): MutableList<Transaction>
}