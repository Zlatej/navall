package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.Member
import cz.cvut.fit.tjv.Navall.models.dtos.MemberDto
import cz.cvut.fit.tjv.Navall.repository.GroupRepo
import cz.cvut.fit.tjv.Navall.repository.MemberRepo
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals

class MemberServiceTest {
    private val memberRepo: MemberRepo = mockk()
    private val groupRepo: GroupRepo = mockk(relaxed = true)
    private lateinit var memberService: MemberService
    private val group = Group(id = 1, name = "group", currency = "X")

    @BeforeEach
    fun setUp() {
        memberService = MemberService(memberRepo, groupRepo)
    }

    @Test
    fun `getAllMembers returns all members`() {
        val members = listOf(
            Member(id = 1, name = "foo", email = "foo@x.y", group = group),
            Member(id = 2, name = "bar", email = "bar@x.y", group = group)
        )
        every { memberRepo.findAll() } returns members

        val result = memberService.getAllMembers()

        assertEquals(result, members)
    }

    @Test
    fun getMembersOfGroup() {

    }

    @Test
    fun `getMember should return member if exists`() {
        val member = Member(id = 1, name = "foo", email = "foo@x.y", group = group)
        every { memberRepo.getMemberById(1) } returns member

        val res = memberService.getMember(1)

        assertEquals(res, member)
    }

    @Test
    fun `getMember should throw exception when not found`() {
        every { memberRepo.getMemberById(any()) } returns null

        val e = assertThrows<ResponseStatusException> { memberService.getMember(1) }

        assertEquals(HttpStatus.NOT_FOUND, e.statusCode)
    }

    @Test
    fun `getMemberByEmail should return user by email`() {
        val member = Member(id = 1, name = "foo", email = "foo@x.y", group = group)
        every { memberRepo.getMemberByEmail("foo@x.y") } returns member
        val res = memberService.getMemberByEmail("foo@x.y")
        assertEquals(res, member)
    }

    @Test
    fun `getMemberByEmail should throw exception when email not found`() {
        every { memberRepo.getMemberByEmail(any()) } returns null
        val e = assertThrows<ResponseStatusException> { memberService.getMemberByEmail("") }
        assertEquals(HttpStatus.NOT_FOUND, e.statusCode)
    }

    @Test
    fun `createMember should return new Member`() {
        val member = Member(id = null, name = "foo", email = "foo@x.y", group = group)
        val memberDto = MemberDto(id = null, name = "foo", email = "foo@x.y", groupId = 1)

        every { memberRepo.existsMemberByEmail("foo@x.y") } returns false
        every { groupRepo.findGroupById(1) } returns group
        every { memberRepo.save(any()) } returns member

        val res = memberService.createMember(memberDto)
        assertEquals(member, res)
    }

    @Test
    fun `createMember should throw exception when email is blank`() {
        val memberDto = MemberDto(id = null, name = "foo", email = "", groupId = 1)

        val e = assertThrows<ResponseStatusException> { memberService.createMember(memberDto) }
        assertEquals(HttpStatus.BAD_REQUEST, e.statusCode)
    }

    @Test
    fun `createMember should throw exception when email already exists`() {
        val memberDto = MemberDto(id = null, name = "foo", email = "foo@x.y", groupId = 1)

        every { memberRepo.existsMemberByEmail("foo@x.y") } returns true

        val e = assertThrows<ResponseStatusException> { memberService.createMember(memberDto) }
        assertEquals(HttpStatus.BAD_REQUEST, e.statusCode)
    }

    @Test
    fun `createMember should throw exception when group does not exist`() {
        val memberDto = MemberDto(id = null, name = "foo", email = "foo@x.y", groupId = 1)

        every { memberRepo.existsMemberByEmail("foo@x.y") } returns false
        every { groupRepo.findGroupById(1) } returns null

        val e = assertThrows<ResponseStatusException> { memberService.createMember(memberDto) }
        assertEquals(HttpStatus.BAD_REQUEST, e.statusCode)
    }

    @Test
    fun deleteMember() {
        val member = Member(
            id = 1,
            name = "foo",
            email = "foo@x.y",
            group = group,
            transactions = mutableListOf(),
            participatedIn = mutableListOf()
        )
        every { memberRepo.delete(member) } returns Unit
        every { memberService.getMember(1) } returns member

        val res = memberService.deleteMember(1)

        assertEquals(res, member)
    }
}