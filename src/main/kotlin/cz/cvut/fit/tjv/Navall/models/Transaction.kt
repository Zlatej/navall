package cz.cvut.fit.tjv.Navall.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "transaction_id")
    val id: Long,

    @Column(name = "type")
    val type: TransactionType,

    @Column(name = "amount")
    val amount: Double,

    @ManyToOne
    @JoinColumn(name = "paid_by")
    val paidBy: Member,

    @Column(name = "created_at")
    val createdAt: LocalDateTime,

    @OneToMany(mappedBy = "transaction")
    val participants: List<TransactionParticipant> = emptyList(),
) {
    enum class TransactionType {
        PAYMENT,
        SETTLEMENT
    }
}
