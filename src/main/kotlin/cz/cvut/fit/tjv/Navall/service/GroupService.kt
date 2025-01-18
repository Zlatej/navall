package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.repository.GroupRepo
import cz.cvut.fit.tjv.Navall.service.dtos.GroupDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class GroupService(
    private val groupRepo: GroupRepo
) {
    fun getAllGroups(): List<Group> = groupRepo.findAll()

    fun getGroupById(id: Long) = groupRepo.findGroupById(id)

    fun createGroup(group: Group) = groupRepo.save(group)

    fun updateGroup(id: Long, group: Group): Group? {
        val existingGroup = groupRepo.findGroupById(id) ?: return null
        val updatedGroup = existingGroup.copy(name = group.name, currency = group.currency)
        groupRepo.save(updatedGroup)
        return updatedGroup
    }
    fun deleteGroup(id: Long): Boolean {
        val group = groupRepo.findGroupById(id) ?: return false
        groupRepo.deleteGroupById(id)
        return true
    }
}