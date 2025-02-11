package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Member
import cz.cvut.fit.tjv.Navall.models.dtos.MemberDto
import cz.cvut.fit.tjv.Navall.repository.GroupRepo
import cz.cvut.fit.tjv.Navall.repository.MemberRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class MemberService(
    private val memberRepo: MemberRepo,
    private val groupRepo: GroupRepo,
) {
    fun getAllMembers(): List<Member> = memberRepo.findAll()

    fun getMembersOfGroup(id: Long): List<Member> = memberRepo.findAllByGroup_Id(id)

    fun getMember(id: Long): Member = memberRepo.getMemberById(id) ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND, "Member with ID $id not found"
    )

    fun getMembers(ids: List<Long>): List<Member> {
        val members = memberRepo.findAllById(ids)
        if (members.size != ids.size) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "One or more member IDs do not exist"
            )
        }
        return members
    }

    fun getMemberByEmail(email: String) = memberRepo.getMemberByEmail(email) ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND, "Email $email not found"
    )

    fun createMember(memberDto: MemberDto): Member {
        if (memberRepo.existsMemberByEmail(memberDto.email)) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Email already exists"
        )
        val group = groupRepo.findGroupById(memberDto.groupId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Group ${memberDto.groupId} not found"
        )
        val member = Member(
            name = memberDto.name,
            email = memberDto.email,
            group = group,
        )
        memberRepo.save(member)
        return member
    }

    fun updateMember(memberDto: MemberDto): Member {
        val existingMember = memberDto.id?.let { memberRepo.getMemberById(it) } ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Member ${memberDto.id} not found"
        )
        val updatedMember = existingMember.copy(
            name = memberDto.name,
            email = memberDto.email,
        )
        memberRepo.save(updatedMember)
        return updatedMember
    }

    fun increaseBalance(member: Member, change: Double): Member {
        val updatedMember = member.copy(
            balance = member.balance + change,
        )
        return memberRepo.save(updatedMember)
    }

    fun decreaseBalance(member: Member, change: Double) {
        val currentBalance = member.balance
        val updatedMember = member.copy(
            balance = member.balance - change,
        )
        memberRepo.save(updatedMember)
    }

    fun deleteMember(id: Long): Member {
        val existingMember =
            memberRepo.getMemberById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User $id not found")
        memberRepo.delete(existingMember)
        return existingMember
    }
}