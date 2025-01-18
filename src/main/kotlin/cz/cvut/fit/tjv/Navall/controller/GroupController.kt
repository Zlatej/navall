package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.service.GroupService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

private val log = KotlinLogging.logger { }

@RestController
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupService,
) {
    @GetMapping
    fun getAllGroups(): ResponseEntity<List<Group>> {
        val groups = groupService.getAllGroups()
        return ResponseEntity.ok(groups)
    }

    @GetMapping("/{id}")
    fun getGroup(@PathVariable id: Long): ResponseEntity<Group> {
        val group = groupService.getGroupById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(group)
    }

    @PostMapping
    fun createGroup(@RequestBody group: Group): ResponseEntity<Group> {
        val savedGroup = groupService.createGroup(group)
        return ResponseEntity.status(201).body(savedGroup)
    }

    @PutMapping("/{id}")
    fun updateGroup(@PathVariable id: Long, @RequestBody group: Group): ResponseEntity<Group> {
        if (id != group.id) {
            return ResponseEntity.badRequest().build()
        }
        val updatedGroup = groupService.updateGroup(id, group) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedGroup)
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Long): ResponseEntity<Void> {
        if (groupService.deleteGroup(id)) return ResponseEntity.ok().build()
        return ResponseEntity.notFound().build()
    }
}