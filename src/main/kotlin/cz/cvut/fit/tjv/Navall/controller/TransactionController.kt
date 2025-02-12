package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.models.Transaction
import cz.cvut.fit.tjv.Navall.models.dtos.SettlementResponse
import cz.cvut.fit.tjv.Navall.models.dtos.TransactionDto
import cz.cvut.fit.tjv.Navall.models.dtos.toDto
import cz.cvut.fit.tjv.Navall.service.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {
    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: Long): ResponseEntity<TransactionDto> =
        ResponseEntity.ok(transactionService.getTransaction(id).toDto())

    @PostMapping
    fun createTransaction(@RequestBody transactionDto: TransactionDto): ResponseEntity<TransactionDto> =
        ResponseEntity.ok(transactionService.createTransaction(transactionDto).toDto())

    @GetMapping("/settlement/{id}")
    fun getSettlement(@PathVariable id: Long): ResponseEntity<List<SettlementResponse>> =
        ResponseEntity.ok(transactionService.getSettlement(id))

    @PostMapping("/{id}")
    fun updateTransaction(@RequestBody transactionDto: TransactionDto): ResponseEntity<TransactionDto> =
        ResponseEntity.ok(transactionService.updateTransaction(transactionDto).toDto())

    @DeleteMapping("/{id}")
    fun deleteTransaction(@PathVariable id: Long): ResponseEntity<Transaction> =
        ResponseEntity.ok(transactionService.deleteTransaction(id))
}