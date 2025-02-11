package cz.cvut.fit.tjv.Navall.models.dtos

import jakarta.validation.constraints.NotBlank

data class MemberDto(
    val id: Long?,
    @field:NotBlank(message = "Name is required")
    val name: String,
    val balance: Double = 0.0,
    @field:NotBlank(message = "Email is required")
    val email: String,
    @field:NotBlank(message = "Group ID is required")
    val groupId: Long,
)