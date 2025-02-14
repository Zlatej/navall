package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.Member
import cz.cvut.fit.tjv.Navall.models.Transaction
import cz.cvut.fit.tjv.Navall.models.TransactionParticipant
import cz.cvut.fit.tjv.Navall.repository.TransactionRepo
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TransactionServiceTest {
    private val transactionRepo: TransactionRepo = mockk()
    private val memberService: MemberService = mockk(relaxed = true)
    private val transactionParticipantService: TransactionParticipantService = mockk(relaxed = true)
    private lateinit var transactionService: TransactionService

    private lateinit var dummyGroup: Group
    private lateinit var member1: Member
    private lateinit var member2: Member

    @BeforeEach
    fun setUp() {
        transactionService = TransactionService(transactionRepo, memberService, transactionParticipantService)

        dummyGroup = Group(
            id = 1,
            name = "TestGroup",
            currency = "USD",
            members = mutableSetOf(),
            transactions = mutableSetOf()
        )
        member1 = Member(
            id = 1,
            name = "Alice",
            balance = 0.0,
            email = "alice@example.com",
            group = dummyGroup,
            transactions = mutableListOf(),
            participatedIn = mutableListOf()
        )
        member2 = Member(
            id = 2,
            name = "Bob",
            balance = 0.0,
            email = "bob@example.com",
            group = dummyGroup,
            transactions = mutableListOf(),
            participatedIn = mutableListOf()
        )
    }

    @Test
    fun `getTransaction returns transaction if it exists`() {
        val transaction = Transaction(
            id = 1,
            type = Transaction.TransactionType.PAYMENT,
            amount = 100.0,
            paidBy = member1,
            group = dummyGroup
        )

        every { transactionRepo.findTransactionById(1) } returns transaction

        val result = transactionService.getTransaction(1)

        assertEquals(transaction, result)
    }

    @Test
    fun `getTransaction throws exception if not found`() {
        every { transactionRepo.findTransactionById(1) } returns null

        val e = assertThrows<ResponseStatusException> { transactionService.getTransaction(1) }
        assertEquals(HttpStatus.NOT_FOUND, e.statusCode)
    }

    @Test
    fun `deleteTransaction deletes transaction successfully`() {
        val participant = TransactionParticipant(
            id = 1,
            percentage = 100.0,
            amountForOne = 100.0,
            transaction = mockk(relaxed = true),
            participant = member2
        )
        val existingTransaction = Transaction(
            id = 1,
            type = Transaction.TransactionType.PAYMENT,
            amount = 100.0,
            paidBy = member1,
            group = dummyGroup,
            participants = mutableListOf(participant)
        )
        every { transactionRepo.findTransactionById(1) } returns existingTransaction
        every { transactionRepo.delete(existingTransaction) } just Runs

        val result = transactionService.deleteTransaction(1)

        verify { memberService.increaseBalance(member2, 100.0) }
        verify { memberService.decreaseBalance(member1, 100.0) }
        verify { transactionRepo.delete(existingTransaction) }
        assertEquals(existingTransaction, result)
    }

    @Test
    fun `getSettlement returns settlements correctly`() {
        member1.balance = -50.0   // debtor
        member2.balance = 70.0    // creditor
        val member3 = Member(
            id = 3,
            name = "Charlie",
            balance = 0.0,
            email = "charlie@example.com",
            group = dummyGroup,
            transactions = mutableListOf(),
            participatedIn = mutableListOf()
        )
        every { memberService.getMembersOfGroup(dummyGroup.id!!) } returns listOf(member1, member2, member3)

        val settlements = transactionService.getSettlement(dummyGroup.id!!)

        assertEquals(1, settlements.size)
        val settlement = settlements[0]
        assertEquals(Transaction.TransactionType.SETTLEMENT, settlement.type)
        assertEquals(50.0, settlement.amount)

        assertEquals(member1.id, settlement.paidById)

        assertEquals(1, settlement.participants.size)
        assertEquals(member2.id, settlement.participants[0].id)
        assertEquals(100.0, settlement.participants[0].percentage)
    }

    @Test
    fun `getSettlement throws exception when group has no members`() {
        every { memberService.getMembersOfGroup(dummyGroup.id!!) } returns emptyList()

        val exception = assertThrows<ResponseStatusException> {
            transactionService.getSettlement(dummyGroup.id!!)
        }
        assertTrue(exception.message.contains("Group does not exist or has no members"))
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }
}