package cz.cvut.fit.tjv.Navall.service.dtos

import jakarta.validation.constraints.NotBlank

data class GroupDto(
    val id: Long? = null,
    @field:NotBlank(message = "Name is required")
    var name: String,
    @field:NotBlank(message = "Currency is required")
    var currency : String,
)