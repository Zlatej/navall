package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "transaction_id")
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: TransactionType = TransactionType.PAYMENT,

    @Column(name = "amount", nullable = false)
    val amount: Double,

    @ManyToOne
    @JoinColumn(name = "paid_by", nullable = false)
    val paidBy: Member,

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "transaction", cascade = [CascadeType.ALL], orphanRemoval = true)
    var participants: MutableList<TransactionParticipant> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,
) {
    enum class TransactionType(val type: String) {
        PAYMENT("payment"),
        SETTLEMENT("settlement")
    }
}
