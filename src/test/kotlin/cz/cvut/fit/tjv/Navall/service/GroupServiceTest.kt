package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.repository.GroupRepo
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals

class GroupServiceTest {
    private val groupRepo: GroupRepo = mockk()
    private val transactionService: TransactionService = mockk(relaxed = true)
    private val memberService: MemberService = mockk(relaxed = true)
    private lateinit var groupService: GroupService

    @BeforeEach
    fun setUp() {
        groupService = GroupService(groupRepo, transactionService, memberService)
    }

    @Test
    fun `getAllGroups returns a list of all groups`() {
        // given
        val groups = listOf(
            Group(id = 1, name = "foo", currency = "EUR"),
            Group(id = 2, name = "bar", currency = "CZK"),
        )
        every { groupRepo.findAll() } returns groups

        // when
        val result = groupService.getAllGroups()
        result.map { println(it.name) }

        // then
        assertEquals(groups, result)
        verify(exactly = 1) { groupRepo.findAll() }
    }

    @Test
    fun `getGroupById returns a group by id when found`() {
        val group = Group(id = 1, name = "foo", currency = "EUR")

        every { groupRepo.findGroupById(1) } returns group
        val result = groupService.getGroupById(1)
        assertEquals(group, result)
        verify(exactly = 1) { groupRepo.findGroupById(1) }
    }

    @Test
    fun `getGroupById throws exreption when not found`() {
        every { groupRepo.findGroupById(1) } returns null

        assertThrows<ResponseStatusException> {
            groupService.getGroupById(1)
        }
    }

    @Test
    fun `createGroup saves and returns a new group`() {
        val group = Group(id = 1, name = "foo", currency = "EUR")
        every { groupRepo.save(group) } returns group

        val result = groupService.createGroup(group)

        assertEquals(group, result)
    }

    @Test
    fun `updateGroup returns updated group`() {
        val group = Group(id = 1, name = "foo", currency = "EUR")
        val groupDto = GroupDto(id = 1, name = "bar", currency = "CZK")
        every { groupRepo.findGroupById(1) } returns group
        every { groupRepo.save(any()) } answers { firstArg() }

        val updatedGroup = groupService.updateGroup(1, groupDto)

        assertEquals(groupDto.name, updatedGroup.name)
        assertEquals(groupDto.currency, updatedGroup.currency)
    }

    @Test
    fun `deleteGroup throws exception when IDs do not match`() {
        val groupDto = GroupDto(id = 1, name = "foo", currency = "EUR")

        assertThrows<ResponseStatusException> {
            groupService.updateGroup(2, groupDto)
        }
    }

    @Test
    fun `deleteGroup returns and deletes group`() {
        val group = Group(id = 1, name = "foo", currency = "EUR")
        every { groupRepo.findGroupById(1) } returns group
        every { groupRepo.deleteGroupByIdNative(1) } just Runs
        val deletedGroup = groupService.deleteGroup(1)
        assertEquals(deletedGroup, group)
        verify(exactly = 1) { groupRepo.deleteGroupByIdNative(1) }
    }
}