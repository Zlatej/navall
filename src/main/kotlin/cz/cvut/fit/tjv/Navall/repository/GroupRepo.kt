package cz.cvut.fit.tjv.Navall.repository

import cz.cvut.fit.tjv.Navall.models.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepo: JpaRepository<Group, Long> {
    fun findGroupById(id: Long): Group?
    fun deleteGroupById(id: Long)
}