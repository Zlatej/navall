package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*

@Entity
@Table(name = "members")
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val id: Long,

    @Column(name = "name")
    val name: String,

    @Column(name = "balance")
    val balance: Double,

    @Column(name = "email", unique = true, nullable = true)
    val email: String? = null,

    @ManyToOne
    @JoinColumn(name = "group")
    val group: Group,

    @OneToMany(mappedBy = "payed_by")
    val transactions: List<Transaction> = emptyList()
)
