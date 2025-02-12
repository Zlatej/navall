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
    var balance: Double = 0.0,

    @Column(name = "email", unique = true, nullable = false)
    val email: String,

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    var group: Group,

    @OneToMany(mappedBy = "paidBy")
    val transactions: MutableList<Transaction> = mutableListOf(),

    @OneToMany(mappedBy = "participant")
    val participatedIn: MutableList<TransactionParticipant> = mutableListOf()
)
