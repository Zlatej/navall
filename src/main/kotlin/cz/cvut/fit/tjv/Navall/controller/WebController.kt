package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.models.dtos.toDto
import cz.cvut.fit.tjv.Navall.models.dtos.toEntity
import cz.cvut.fit.tjv.Navall.service.GroupService
import cz.cvut.fit.tjv.Navall.service.MemberService
import cz.cvut.fit.tjv.Navall.service.TransactionService
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
        groupService.createGroup(groupDto.toEntity())
        return "redirect:/groups"
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

    @GetMapping("/groups/member/email")
    fun fundGroupByEmail(@RequestParam email: String, model: Model): String {
        return try {
            val member = memberService.getMemberByEmail(email)
            val group = member.group
            val members = group.id?.let { it1 -> memberService.getMembersOfGroup(it1).map { it.toDto() } }
            val transactions = group.id?.let { it1 -> transactionService.getTransactionsOfGroup(it1).map { it.toDto() } }

            model.addAttribute("group", group.toDto())
            model.addAttribute("members", members)
            model.addAttribute("transactions", transactions)

            "groups/group_detail"
        } catch (ex: ResponseStatusException) {
            model.addAttribute("errorMessage", "Member with email $email not found") // todo
            "error/404"
        }
    }
}
