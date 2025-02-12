package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.repository.GroupRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class GroupService(
    private val groupRepo: GroupRepo
) {
    fun getAllGroups(): List<Group> = groupRepo.findAll()

    fun getGroupById(id: Long) = groupRepo.findGroupById(id) ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND, "Group with ID $id not found"
    )

    fun createGroup(group: Group) = groupRepo.save(group)

    fun updateGroup(id: Long, group: GroupDto): Group {
        if (id != group.id) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Group IDs do not match")
        val existingGroup = groupRepo.findGroupById(id) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Group with ID $id not found"
        )
        val updatedGroup = existingGroup.copy(
            name = group.name, currency = group.currency
        )
        groupRepo.save(updatedGroup)
        return updatedGroup
    }

    fun deleteGroup(id: Long) {
        val existingGroup = getGroupById(id)
        groupRepo.delete(existingGroup)
    }
}