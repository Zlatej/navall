package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.models.Transaction
import cz.cvut.fit.tjv.Navall.models.dtos.*
import cz.cvut.fit.tjv.Navall.service.GroupService
import cz.cvut.fit.tjv.Navall.service.MemberService
import cz.cvut.fit.tjv.Navall.service.TransactionService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@Controller
class WebController(
    private val groupService: GroupService,
    private val memberService: MemberService,
    private val transactionService: TransactionService
) {
    // landing page
    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    // group list
    @GetMapping("/groups")
    fun listGroups(model: Model): String {
        val groups = groupService.getAllGroups().map { it.toDto() }
        model.addAttribute("groups", groups)
        return "groups/groups"
    }

    // new group form
    @GetMapping("/groups/create")
    fun showCreateForm(model: Model): String {
        model.addAttribute("group", GroupDto(name = "", currency = ""))
        return "groups/group_form"
    }

    // post new group
    @PostMapping("/groups/create")
    fun createGroup(@ModelAttribute("group") groupDto: GroupDto): String {
        val createdGroup = groupService.createGroup(groupDto.toEntity())
        return "redirect:/groups/${createdGroup.id}"
    }

    // group detail
    @GetMapping("/groups/{id}")
    fun getGroup(@PathVariable id: Long, model: Model): String {
        val group = groupService.getGroupById(id)
        val members = memberService.getMembersOfGroup(id).map { it.toDto() }
        val transactions =
            group.id?.let { it -> transactionService.getTransactionsOfGroup(it) }
        val webTransactions: MutableList<WebTransactionDto> = mutableListOf()

        transactions?.forEach{ t ->
            val pNames: MutableList<String> = mutableListOf()
            t.participants.forEach { p-> pNames.add(p.participant.name) }
            webTransactions.add(WebTransactionDto(
                id = t.id,
                type = t.type,
                amount = t.amount,
                paidById = t.paidBy.id,
                paidByName = t.paidBy.name,
                createdAt = t.createdAt,
                participants = t.participants.map { it.toDto() },
                participantNames = pNames
            ))
        }
        model.addAttribute("group", group.toDto())
        model.addAttribute("members", members)
        model.addAttribute("transactions", webTransactions)
        return "groups/group_detail"
    }

    // goes to group by user email
    @GetMapping("/groups/member/email")
    fun fundGroupByEmail(@RequestParam email: String, model: Model): String {
        return try {
            val member = memberService.getMemberByEmail(email)
            val group = member.group
            val members = group.id?.let { it1 -> memberService.getMembersOfGroup(it1).map { it.toDto() } }
            val transactions =
                group.id?.let { it -> transactionService.getTransactionsOfGroup(it) }
            val webTransactions: MutableList<WebTransactionDto> = mutableListOf()

            transactions?.forEach{ t ->
                val pNames: MutableList<String> = mutableListOf()
                t.participants.forEach { p-> pNames.add(p.participant.name) }
                webTransactions.add(WebTransactionDto(
                    id = t.id,
                    type = t.type,
                    amount = t.amount,
                    paidById = t.paidBy.id,
                    paidByName = t.paidBy.name,
                    createdAt = t.createdAt,
                    participants = t.participants.map { it.toDto() },
                    participantNames = pNames
                ))
            }

            webTransactions.forEach { println(it.paidByName) }

            model.addAttribute("group", group.toDto())
            model.addAttribute("members", members)
            model.addAttribute("transactions", webTransactions.toList())

            "groups/group_detail"
        } catch (ex: ResponseStatusException) {
            model.addAttribute("errorMessage", "Member with email $email not found") // todo
            "error/404"
        }
    }

    // edit group with group_form
    @GetMapping("/groups/edit/{id}")
    fun editGroup(@PathVariable id: Long, model: Model): String {
        val group = groupService.getGroupById(id)
        model.addAttribute("group", group.toDto())
        return "groups/group_form"
    }

    // delete and go to list
    @PostMapping("/groups/delete/{id}")
    fun removeGroup(@PathVariable id: Long): String {
        groupService.deleteGroup(id)
        return "redirect:/groups"
    }


    // Manage Members for Group
    @GetMapping("/groups/{groupId}/members")
    fun manageGroupMembers(@PathVariable groupId: Long, model: Model): String {
        val group = groupService.getGroupById(groupId).toDto()
        val members = memberService.getMembersOfGroup(groupId).map { it.toDto() }
        model.addAttribute("group", group)
        model.addAttribute("members", members)
        return "members/manage_members"
    }

    // Show Create Member Form
    @GetMapping("/groups/{groupId}/members/create")
    fun showCreateMemberForm(@PathVariable groupId: Long, model: Model): String {
        val newMember = MemberDto(
            id = null,
            name = "",
            email = "",
            balance = 0.0,
            groupId = groupId
        )
        model.addAttribute("newMember", newMember)
        model.addAttribute("groupId", groupId)
        return "members/member_form"
    }

    // Process Create Member Form Submission
    @PostMapping("/groups/{groupId}/members/create")
    fun createMember(@PathVariable groupId: Long, @ModelAttribute("newMember") memberDto: MemberDto): String {
        try {
            memberService.createMember(memberDto)
        }
        catch (ex: ResponseStatusException) {
            return "error/400"
        }
        return "redirect:/groups/$groupId/members"
    }

    // Show Edit Member Form
    @GetMapping("/groups/{groupId}/members/{memberId}/edit")
    fun showEditMemberForm(@PathVariable groupId: Long,
                           @PathVariable memberId: Long,
                           model: Model): String {
        val member = memberService.getMember(memberId)
        val memberDto = MemberDto(
            id = member.id,
            name = member.name,
            email = member.email,
            balance = member.balance,
            groupId = member.group.id ?: groupId
        )
        model.addAttribute("member", memberDto)
        model.addAttribute("groupId", groupId)
        return "members/member_edit_form"
    }

    // Process Edit Member Form Submission
    @PostMapping("/groups/{groupId}/members/{memberId}/edit")
    fun editMember(@PathVariable groupId: Long,
                   @PathVariable memberId: Long,
                   @ModelAttribute("member") memberDto: MemberDto): String {
        memberService.updateMember(memberId, memberDto)
        return "redirect:/groups/$groupId/members"
    }



    // gets settlement suggestion
    @GetMapping("/groups/{id}/settlement")
    fun getSettlement(@PathVariable id: Long, model: Model): String {
        val settlements = transactionService.getSettlement(id)
        model.addAttribute("settlements", settlements)
        model.addAttribute("groupId", id)
        return "groups/settlement"
    }

    // applies suggested settlement
    @Transactional
    @PostMapping("/groups/{id}/settlement/apply")
    fun acceptSettlement(
        @PathVariable id: Long,
        @RequestParam paidById: Long,
        @RequestParam creditorId: Long,
        @RequestParam amount: Double,
    ): String {
        val settlement = TransactionDto(
            type = Transaction.TransactionType.SETTLEMENT,
            amount = amount,
            paidById = paidById,
            participants = listOf(
                TransactionParticipantDto(
                    id = creditorId,
                    percentage = 100.0,
                )
            ),
        )
        transactionService.createTransaction(settlement)

        return "redirect:/groups/${id}/settlement"
    }

    // creates form for custom transaction
    @GetMapping("/groups/{groupId}/transactions/create")
    fun showTransactionForm(@PathVariable groupId: Long, model: Model): String {
        val members = memberService.getMembersOfGroup(groupId).map { it.toDto() }
        model.addAttribute("members", members)
        model.addAttribute("groupId", groupId)

        // Pre-populate a new TransactionDto with one empty participant
        val newTransaction = TransactionDto(
            id = null,
            type = Transaction.TransactionType.PAYMENT,
            amount = 0.0,
            paidById = members.firstOrNull()?.id ?: 0L,
            createdAt = null,
            participants = mutableListOf(TransactionParticipantDto(id = null, percentage = 0.0))
        )
        model.addAttribute("newTransaction", newTransaction)
        return "transactions/transaction_form"
    }

    @PostMapping("/groups/{groupId}/transactions/create")
    fun createTransaction(
        @PathVariable groupId: Long,
        @ModelAttribute newTransaction: TransactionDto
    ): String {
        transactionService.createTransaction(newTransaction)
        return "redirect:/groups/$groupId"
    }

    @GetMapping("/groups/{groupId}/transactions/edit/{transactionId}")
    fun editTransaction(
        @PathVariable groupId: Long,
        @PathVariable transactionId: Long,
        model: Model
    ): String {
        val transaction = transactionService.getTransaction(transactionId).toDto()
        val members = memberService.getMembersOfGroup(groupId).map { it.toDto() }

        model.addAttribute("newTransaction", transaction)
        model.addAttribute("groupId", groupId)
        model.addAttribute("members", members)

        return "transactions/transaction_form"
    }

}
