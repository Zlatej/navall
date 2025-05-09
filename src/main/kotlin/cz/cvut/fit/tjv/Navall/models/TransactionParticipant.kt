package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*

@Entity
@Table(name = "transaction_participants")
data class TransactionParticipant(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "transaction_participant_id")
    val id: Long? = null,

    @Column(name = "percentage")
    val percentage: Double,

    @Column(name = "amount_for_one")
    val amountForOne: Double,

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    val transaction: Transaction,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val participant: Member
)
