package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "group_id")
    val id: Long? = null,

    @Column(name = "group_name", nullable = false)
    val name: String,

    @Column(name = "currency", nullable = false)
    val currency: String,

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
    val members: Set<Member> = emptySet(),
)
