package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    val id: Long,

    @Column(name = "currency")
    val currency: String,

    @OneToMany(mappedBy = "group")
    val members: List<Member>,
)
