package cz.cvut.fit.tjv.Navall.service.dtos

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.Member

fun Group.toDto() = GroupDto(
        id = this.id,
        name = this.name,
        currency = this.currency,
    )

fun GroupDto.toEntity() = Group(
        id = this.id,
        name = this.name,
        currency = this.currency,
    )

fun Member.toDto() = MemberDto(
    id = this.id,
    name = this.name,
    balance = this.balance,
    email = this.email,
    group = this.group.toDto(),
)
