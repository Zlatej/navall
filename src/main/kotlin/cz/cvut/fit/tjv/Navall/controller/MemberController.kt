package cz.cvut.fit.tjv.Navall.controller


import cz.cvut.fit.tjv.Navall.service.MemberService
import cz.cvut.fit.tjv.Navall.service.dtos.MemberDto
import cz.cvut.fit.tjv.Navall.service.dtos.toDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/members")
class MemberController(
    val memberService: MemberService
) {
    @GetMapping
    fun getMembers(): ResponseEntity<List<MemberDto>> = ResponseEntity.ok(memberService.getMembers().map { it.toDto() })

    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.getMember(id).toDto())

    @GetMapping("/email")
    fun getMemberByEmail(@RequestParam email: String): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.getMemberByEmail(email).toDto())

    @PostMapping
    fun createMember(@RequestBody member: MemberDto): ResponseEntity<MemberDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(member).toDto())

    @PutMapping
    fun updateMember(@RequestBody member: MemberDto): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.updateMember(member).toDto())


    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<MemberDto> =
        ResponseEntity.ok(memberService.deleteMember(id).toDto())
}

