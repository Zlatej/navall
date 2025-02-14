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
        val tranactions = transactionService.getTransactionsOfGroup(id).map { it.toDto() }
        model.addAttribute("group", group.toDto())
        model.addAttribute("members", members)
        model.addAttribute("transactions", tranactions)
        return "groups/group_detail"
    }

    // member management
    @GetMapping("/groups/{id}/members")
    fun manageGroupMembers(@PathVariable id: Long, model: Model): String {
        val group = groupService.getGroupById(id).toDto()
        val members = memberService.getMembersOfGroup(id).map { it.toDto() }

        model.addAttribute("group", group)
        model.addAttribute("members", members)
        return "groups/manage_members"
    }

    // goes to group by user email
    @GetMapping("/groups/member/email")
    fun fundGroupByEmail(@RequestParam email: String, model: Model): String {
        return try {
            val member = memberService.getMemberByEmail(email)
            val group = member.group
            val members = group.id?.let { it1 -> memberService.getMembersOfGroup(it1).map { it.toDto() } }
            val transactions =
                group.id?.let { it1 -> transactionService.getTransactionsOfGroup(it1).map { it.toDto() } }

            model.addAttribute("group", group.toDto())
            model.addAttribute("members", members)
            model.addAttribute("transactions", transactions)

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
}
