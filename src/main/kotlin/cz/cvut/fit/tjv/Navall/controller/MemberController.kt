package cz.cvut.fit.tjv.Navall.controller


import cz.cvut.fit.tjv.Navall.models.dtos.MemberDto
import cz.cvut.fit.tjv.Navall.models.dtos.toDto
import cz.cvut.fit.tjv.Navall.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/members")
class MemberController(
    val memberService: MemberService
) {
    @Operation(summary = "Get member by ID", description = "Returns member by ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Member returned successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Member not found",
            )
        ]
    )
    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.getMember(id).toDto())

    @Operation(summary = "Get all members", description = "Returns all members")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Members returned successfully",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = MemberDto::class))
                )]
            ),
        ]
    )
    @GetMapping
    fun getMembers(): ResponseEntity<List<MemberDto>> =
        ResponseEntity.ok(memberService.getAllMembers().map { it.toDto() })

    @Operation(summary = "Get member by email", description = "Returns member by email")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Member returned successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Member not found",
            )
        ]
    )
    @GetMapping("/email")
    fun getMemberByEmail(@RequestParam email: String): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.getMemberByEmail(email).toDto())

    @Operation(summary = "Get members by group ID", description = "Returns all members of a group")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Members returned successfully",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = MemberDto::class))
                )]
            ),
        ]
    )
    @GetMapping("/group/{id}")
    fun getMembersOfGroup(@PathVariable id: Long): ResponseEntity<List<MemberDto>> =
        ResponseEntity.ok(memberService.getMembersOfGroup(id).map { it.toDto() })

    @Operation(summary = "Creates member", description = "Returns created member")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Member returned successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Email already exists"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Group not found",
            ),
        ]
    )
    @PostMapping
    fun createMember(@RequestBody member: MemberDto): ResponseEntity<MemberDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(member).toDto())

    @Operation(summary = "Updates member", description = "ONLY NAME CAN BE UPDATED. Returns updated member.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Member returned successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Member not found",
            ),
        ]
    )
    @PutMapping("/{id}")
    fun updateMember(@PathVariable id: Long, @RequestBody member: MemberDto): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.updateMember(id, member).toDto())

    @Operation(summary = "Deletes member", description = "Returns deleted member")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Member deleted successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = MemberDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Member not found",
            ),
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.deleteMember(id).toDto())
}

