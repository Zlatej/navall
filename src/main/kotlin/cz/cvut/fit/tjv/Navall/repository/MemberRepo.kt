package cz.cvut.fit.tjv.Navall.repository

import cz.cvut.fit.tjv.Navall.models.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepo : JpaRepository<Member, Long> {
    fun getMemberById(id: Long): Member?
    fun getMemberByEmail(email: String): Member?
    fun existsMemberByEmail(email: String): Boolean
}