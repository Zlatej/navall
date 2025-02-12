package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.models.dtos.SettlementResponse
import cz.cvut.fit.tjv.Navall.models.dtos.TransactionDto
import cz.cvut.fit.tjv.Navall.models.dtos.toDto
import cz.cvut.fit.tjv.Navall.service.TransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {
    @Operation(summary = "Get transaction by ID", description = "Returns a transaction by its ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Transaction returned successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TransactionDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Transaction not found",
            )
        ]
    )
    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: Long): ResponseEntity<TransactionDto> =
        ResponseEntity.ok(transactionService.getTransaction(id).toDto())

    @Operation(summary = "Get transactions by group ID", description = "Returns all transactions of a group")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Transactions returned successfully",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = TransactionDto::class))
                )]
            ),
        ]
    )
    @GetMapping("/group/{id}")
    fun getMembersOfGroup(@PathVariable id: Long): ResponseEntity<List<TransactionDto>> =
        ResponseEntity.ok(transactionService.getTransactionsOfGroup(id).map { it.toDto() })

    @Operation(summary = "Create transaction", description = "Returns created transaction")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Transaction created successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TransactionDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
            ),
        ]
    )
    @PostMapping
    fun createTransaction(@RequestBody transactionDto: TransactionDto): ResponseEntity<TransactionDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionDto).toDto())

    @Operation(summary = "Update transaction", description = "Returns updated transaction")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Transaction updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TransactionDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Transaction not found",
            ),
        ]
    )
    @PostMapping("/{id}")
    fun updateTransaction(@RequestBody transactionDto: TransactionDto): ResponseEntity<TransactionDto> =
        ResponseEntity.ok(transactionService.updateTransaction(transactionDto).toDto())

    @Operation(summary = "Update transaction", description = "Returns updated transaction")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Transaction updated successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TransactionDto::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Transaction not found",
            ),
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteTransaction(@PathVariable id: Long): ResponseEntity<TransactionDto> =
        ResponseEntity.ok(transactionService.deleteTransaction(id).toDto())

    @Operation(
        summary = "Get settlement",
        description = "Returns suggested transactions to settle up members of a group"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Transactions returned successfully",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = SettlementResponse::class))
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Group or members not found",
            ),
        ]
    )
    @GetMapping("/settlement/{id}")
    fun getSettlement(@PathVariable id: Long): ResponseEntity<List<SettlementResponse>> =
        ResponseEntity.ok(transactionService.getSettlement(id))
}