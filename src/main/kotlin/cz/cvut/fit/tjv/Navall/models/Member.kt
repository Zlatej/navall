package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*

@Entity
@Table(name = "members")
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_id")
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "balance")
    val balance: Double = 0.0,

    @Column(name = "email", unique = true, nullable = false)
    val email: String,

    @ManyToOne
    @JoinColumn(name = "group_id")
    val group: Group,

    @OneToMany(mappedBy = "paidBy")
    val transactions: List<Transaction> = emptyList(),

    @OneToMany(mappedBy = "participant")
    val participatedIn: List<TransactionParticipant> = emptyList(),
)
