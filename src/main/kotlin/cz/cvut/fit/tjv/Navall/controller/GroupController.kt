package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.service.GroupService
import cz.cvut.fit.tjv.Navall.service.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.toDto
import cz.cvut.fit.tjv.Navall.toEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupService,
) {
    @GetMapping
    fun getAllGroups(): ResponseEntity<List<GroupDto>> {
        return ResponseEntity.ok(groupService.getAllGroups().map { it.toDto() })
    }

    @GetMapping("/{id}")
    fun getGroup(@PathVariable id: Long): ResponseEntity<GroupDto> {
        val group = groupService.getGroupById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(group.toDto())
    }

    @PostMapping
    fun createGroup(@RequestBody group: GroupDto): ResponseEntity<GroupDto> {
        val savedGroup = groupService.createGroup(group.toEntity())
        return ResponseEntity.status(201).body(savedGroup.toDto())
    }

    @PutMapping("/{id}")
    fun updateGroup(@PathVariable id: Long, @RequestBody group: GroupDto): ResponseEntity<GroupDto> {
        if (id != group.id) {
            return ResponseEntity.badRequest().build()
        }
        val updatedGroup = groupService.updateGroup(id, group) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedGroup.toDto())
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Long): ResponseEntity<Void> {
        if (groupService.deleteGroup(id)) return ResponseEntity.ok().build()
        return ResponseEntity.notFound().build()
    }
}