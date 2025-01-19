package cz.cvut.fit.tjv.Navall.service

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.repository.GroupRepo
import cz.cvut.fit.tjv.Navall.service.dtos.GroupDto
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepo: GroupRepo
) {
    fun getAllGroups(): List<Group> = groupRepo.findAll()

    fun getGroupById(id: Long) = groupRepo.findGroupById(id)

    fun createGroup(group: Group) = groupRepo.save(group)

    fun updateGroup(id: Long, group: GroupDto): Group? {
        val existingGroup = groupRepo.findGroupById(id) ?: return null
        val updatedGroup = existingGroup.copy(
            name = group.name,
            currency = group.currency
        )
        groupRepo.save(updatedGroup)
        return updatedGroup
    }

    fun deleteGroup(id: Long): Boolean {
        if (!groupRepo.existsById(id)) return false
        groupRepo.deleteById(id)
        return true
    }
}