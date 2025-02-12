package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.models.dtos.toDto
import cz.cvut.fit.tjv.Navall.models.dtos.toEntity
import cz.cvut.fit.tjv.Navall.service.GroupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupService,
) {
    @GetMapping
    fun getAllGroups(): ResponseEntity<List<GroupDto>> =
        ResponseEntity.ok(groupService.getAllGroups().map { it.toDto() })

    @GetMapping("/{id}")
    fun getGroup(@PathVariable id: Long): ResponseEntity<GroupDto> =
        ResponseEntity.ok(groupService.getGroupById(id).toDto())

    @PostMapping
    fun createGroup(@RequestBody group: GroupDto): ResponseEntity<GroupDto> =
        ResponseEntity.status(201).body(groupService.createGroup(group.toEntity()).toDto())

    @PutMapping("/{id}")
    fun updateGroup(@PathVariable id: Long, @RequestBody group: GroupDto): ResponseEntity<GroupDto> =
        ResponseEntity.ok(groupService.updateGroup(id, group).toDto())

    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Long): ResponseEntity<Long> =
        ResponseEntity.ok(groupService.deleteGroup(id))
}