package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Member
import cz.cvut.fit.tjv.Navall.models.Transaction
import cz.cvut.fit.tjv.Navall.models.TransactionParticipant
import cz.cvut.fit.tjv.Navall.models.dtos.SettlementResponse
import cz.cvut.fit.tjv.Navall.models.dtos.TransactionDto
import cz.cvut.fit.tjv.Navall.models.dtos.TransactionParticipantDto
import cz.cvut.fit.tjv.Navall.repository.TransactionRepo
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.math.min

@Service
class TransactionService(
    private val transactionRepo: TransactionRepo,
    private val memberService: MemberService,
    private val transactionParticipantService: TransactionParticipantService,
) {
    fun getTransaction(id: Long) = transactionRepo.findTransactionById(id) ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND, "Transaction with ID $id not found"
    )

    fun getTransactionsOfGroup(groupId: Long): List<Transaction> =
        transactionRepo.getTransactionsByGroup_Id(groupId)

    @Transactional
    fun createTransaction(transactionDto: TransactionDto): Transaction {
        checkTransactionDto(transactionDto)

        val paidBy = memberService.getMember(transactionDto.paidById)
        memberService.increaseBalance(paidBy, transactionDto.amount)

        val transaction = Transaction(
            type = transactionDto.type, amount = transactionDto.amount, paidBy = paidBy, group = paidBy.group
        )

        val savedTransaction = transactionRepo.save(transaction)

        val participants = processParticipants(transactionDto, savedTransaction)

        participants.map { transactionParticipantService.saveTransactionParticipant(it) }

        savedTransaction.participants = participants
        return transactionRepo.save(savedTransaction)
    }

    @Transactional
    fun updateTransaction(transactionDto: TransactionDto): Transaction {
        transactionDto.id ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "TransactionId is mandatory",
        )
        checkTransactionDto(transactionDto)

        val existingTransaction = getTransaction(transactionDto.id)
        revertParticipants(existingTransaction)
        val newParticipants = processParticipants(transactionDto, existingTransaction)

        var newPaidBy = existingTransaction.paidBy
        if (existingTransaction.paidBy.id != transactionDto.paidById || existingTransaction.amount != transactionDto.amount) {
            memberService.increaseBalance(existingTransaction.paidBy, existingTransaction.amount)
            newPaidBy = memberService.getMember(transactionDto.paidById)
            memberService.decreaseBalance(newPaidBy, transactionDto.amount)
        }

        val updatedTransaction = existingTransaction.copy(
            amount = transactionDto.amount,
            paidBy = newPaidBy,
            participants = newParticipants,
        )

        return transactionRepo.save(updatedTransaction)
    }

    // calculates and updates members who participated in transaction
    private fun processParticipants(
        transactionDto: TransactionDto, savedTransaction: Transaction
    ): MutableList<TransactionParticipant> {
        val participants: MutableList<TransactionParticipant> = mutableListOf()

        for (participantDto in transactionDto.participants) {
            if (participantDto.id == null) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Participant has no id")
            }

            val member = memberService.getMember(participantDto.id)
            val amountForOne = transactionDto.amount * participantDto.percentage / 100

            memberService.decreaseBalance(member, amountForOne)

            val tp = TransactionParticipant(
                percentage = participantDto.percentage,
                amountForOne = amountForOne,
                transaction = savedTransaction,
                participant = member
            )
            participants.add(tp)
        }
        return participants
    }

    // refunds balance to participants in a transaction
    private fun revertParticipants(existingTransaction: Transaction) {
        for (participant in existingTransaction.participants) {
            memberService.increaseBalance(participant.participant, participant.amountForOne)
        }
    }

    private fun checkTransactionDto(transactionDto: TransactionDto) {
        val paidBy = memberService.getMember(transactionDto.paidById)
        val groupId = paidBy.group.id

        if (transactionDto.amount <= 0) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Amount must be greater than zero"
        )

        if (transactionDto.type == Transaction.TransactionType.SETTLEMENT && transactionDto.participants.size != 1) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Participants in settlement has to be exactly 1 participant"
        )

        var percentageSum = 0.0
        for (participantDto in transactionDto.participants) {
            val participant = participantDto.id?.let { memberService.getMember(it) } ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
            if (participant.group.id != groupId) throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Participant ${participant.id} does not belong to group $groupId"
            )
            percentageSum += participantDto.percentage
        }

        if (!percentageSum.equals(100.0)) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Percentages must sum to 100, current sum is $percentageSum"
        )
    }

    @Transactional
    fun deleteTransaction(transactionId: Long): Transaction {
        val existingTransaction = getTransaction(transactionId)

        revertParticipants(existingTransaction)
        memberService.decreaseBalance(existingTransaction.paidBy, existingTransaction.amount)

        transactionRepo.delete(existingTransaction)
        return existingTransaction
    }

    fun getSettlement(groupId: Long): List<SettlementResponse> {
        val groupMembers = memberService.getMembersOfGroup(groupId)
        if (groupMembers.isEmpty()) throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Group does not exist or has no members"
        )

        val debtors = mutableListOf<Member>()
        val creditors = mutableListOf<Member>()

        for (member in groupMembers) {
            when {
                (member.balance > 0.0) -> creditors.add(member)
                (member.balance < 0.0) -> debtors.add(member)
            }
        }

        debtors.sortBy { it.balance }
        creditors.sortByDescending { it.balance }

        val settlements = mutableListOf<SettlementResponse>()

        var i = 0
        var j = 0
        while (i < debtors.size && j < creditors.size) {
            val debtor = debtors[i]
            val creditor = creditors[j]

            if (debtor.balance == 0.0) {
                i++
                continue
            }
            if (creditor.balance == 0.0) {
                j++
                continue
            }

            val maxAmount = min(-debtor.balance, creditor.balance)
            settlements.add(
                SettlementResponse(
                    type = Transaction.TransactionType.SETTLEMENT,
                    amount = maxAmount,
                    paidById = debtor.id ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR),
                    participants = listOf(
                        TransactionParticipantDto(
                            id = creditor.id, percentage = 100.0
                        )
                    )
                )
            )
            debtor.balance += maxAmount
            creditor.balance -= maxAmount

        }
        return settlements
    }
}