package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Member
import cz.cvut.fit.tjv.Navall.models.dtos.MemberDto
import cz.cvut.fit.tjv.Navall.repository.GroupRepo
import cz.cvut.fit.tjv.Navall.repository.MemberRepo
import jakarta.transaction.Transactional
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

    fun getMemberByEmail(email: String) = memberRepo.getMemberByEmail(email) ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND, "Email $email not found"
    )

    @Transactional
    fun createMember(memberDto: MemberDto): Member {
        if (memberDto.email.isBlank()) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Email is mandatory"
        )
        if (memberRepo.existsMemberByEmail(memberDto.email)) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Email already exists"
        )
        val group = groupRepo.findGroupById(memberDto.groupId) ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Group ${memberDto.groupId} not found"
        )
        val member = Member(
            name = memberDto.name,
            email = memberDto.email,
            group = group,
        )
        memberRepo.save(member)
        return member
    }

    @Transactional
    fun updateMember(id: Long, memberDto: MemberDto): Member {
        if (memberDto.id == null) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "ID is mandatory"
        )
        if (id != memberDto.id) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Member ID does not match ID in path"
        )
        val existingMember = getMember(id)
        if (existingMember.email != memberDto.email) throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Email cannot be updated"
        )
        val updatedMember = existingMember.copy(
            name = memberDto.name,
        )
        memberRepo.save(updatedMember)
        return updatedMember
    }

    @Transactional
    fun deleteMember(id: Long): Member {
        val existingMember = getMember(id)
        if (existingMember.transactions.isNotEmpty() || existingMember.participatedIn.isNotEmpty()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot delete member $id as they have existing transactions."
            )
        }
        memberRepo.delete(existingMember)
        return existingMember
    }

    fun increaseBalance(member: Member, change: Double): Member {
        val updatedMember = member.copy(
            balance = member.balance + change,
        )
        return memberRepo.save(updatedMember)
    }

    fun decreaseBalance(member: Member, change: Double) {
        val updatedMember = member.copy(
            balance = member.balance - change,
        )
        memberRepo.save(updatedMember)
    }
}
