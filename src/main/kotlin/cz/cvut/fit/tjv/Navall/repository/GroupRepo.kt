package cz.cvut.fit.tjv.Navall.repository

import cz.cvut.fit.tjv.Navall.models.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupRepo : JpaRepository<Group, Long> {
    fun findGroupById(id: Long): Group?

    @Modifying
    @Query(
        value = """
        DELETE FROM transaction_participants
            WHERE transaction_id IN (SELECT transaction_id FROM transactions WHERE group_id = :id);
        DELETE FROM transactions WHERE group_id = :id;
        DELETE FROM members WHERE group_id = :id;
        DELETE FROM groups WHERE group_id = :id;
        """,
        nativeQuery = true
    )
    fun deleteGroupByIdNative(@Param("id") id: Long)
}