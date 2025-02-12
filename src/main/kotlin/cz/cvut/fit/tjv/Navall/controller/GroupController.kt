package cz.cvut.fit.tjv.Navall.controller

import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.models.dtos.toDto
import cz.cvut.fit.tjv.Navall.models.dtos.toEntity
import cz.cvut.fit.tjv.Navall.service.GroupService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupService,
) {
    @Operation(summary = "Get all groups", description = "Returns all groups")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Groups returned successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GroupDto::class))]
            ),
        ]
    )
    @GetMapping
    fun getAllGroups(): ResponseEntity<List<GroupDto>> =
        ResponseEntity.ok(groupService.getAllGroups().map { it.toDto() })

    @Operation(summary = "Get group by ID", description = "Returns a group by its ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Group returned successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GroupDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Group not found",
            )
        ]
    )
    @GetMapping("/{id}")
    fun getGroup(@PathVariable id: Long): ResponseEntity<GroupDto> =
        ResponseEntity.ok(groupService.getGroupById(id).toDto())

    @Operation(summary = "Create group", description = "Returns created group")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Group created successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GroupDto::class))]
            ),
        ]
    )
    @PostMapping
    fun createGroup(@RequestBody group: GroupDto): ResponseEntity<GroupDto> =
        ResponseEntity.status(201).body(groupService.createGroup(group.toEntity()).toDto())

    @Operation(summary = "Update group", description = "Returns updated group")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Group updated successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GroupDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Group ID does not match ID in path"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Group not found",
            )
        ]
    )
    @PutMapping("/{id}")
    fun updateGroup(@PathVariable id: Long, @RequestBody group: GroupDto): ResponseEntity<GroupDto> =
        ResponseEntity.ok(groupService.updateGroup(id, group).toDto())

    @Operation(summary = "Delete group", description = "Returns deleted group")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Group deleted successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = GroupDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Group not found",
            )
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Long): ResponseEntity<GroupDto> =
        ResponseEntity.ok(groupService.deleteGroup(id).toDto())
}