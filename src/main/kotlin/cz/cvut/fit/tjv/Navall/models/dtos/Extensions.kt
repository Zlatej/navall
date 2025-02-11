package cz.cvut.fit.tjv.Navall.models.dtos

import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.Member
import cz.cvut.fit.tjv.Navall.models.Transaction
import cz.cvut.fit.tjv.Navall.models.TransactionParticipant
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

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
    groupId = this.group.id ?: throw ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Member ${this.id} has no group ID"
    )
)

fun TransactionParticipant.toDto() = TransactionParticipantDto(
    id = this.id,
    percentage = this.percentage,
)

fun Transaction.toDto() = TransactionDto(
    id = this.id,
    type = this.type,
    amount = this.amount,
    paidById = this.paidBy.id ?: throw ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Transaction ${this.id} has no paid by ID"
    ),
    createdAt = this.createdAt,
    participants = this.participants.map { participant -> participant.toDto() },
)

