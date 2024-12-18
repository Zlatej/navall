package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "group_id")
    val id: Long,

    @Column(name = "currency")
    val currency: String,

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL])
    val members: Set<Member>,
)
