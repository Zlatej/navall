package cz.cvut.fit.tjv.Navall

import com.fasterxml.jackson.databind.ObjectMapper
import cz.cvut.fit.tjv.Navall.models.Transaction
import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.models.dtos.MemberDto
import cz.cvut.fit.tjv.Navall.models.dtos.TransactionDto
import cz.cvut.fit.tjv.Navall.models.dtos.TransactionParticipantDto
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberOperationsTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun `members who have transaction cannot be deleted`() {
        val groupDto = GroupDto(name = "foo", currency = "USD")

        // create group
        val response = mockMvc.post("/api/groups") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(groupDto)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
            .andReturn()
            .response
            .contentAsString

        val createdGroupDto = objectMapper.readValue(response, GroupDto::class.java)
        assertNotNull(createdGroupDto.id)

        // ALICE
        val aliceDto = MemberDto(id = null, name = "alice", email = "alice@foo.bar", groupId = createdGroupDto.id!!)
        val responseMemberAlice = mockMvc.post("/api/members") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(aliceDto)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
            .andReturn()
            .response
            .contentAsString

        // BOB
        val bobDto = MemberDto(id = null, name = "bob", email = "bob@foo.bar", groupId = createdGroupDto.id!!)
        val responseMemberBob = mockMvc.post("/api/members") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bobDto)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
            .andReturn()
            .response
            .contentAsString

        val createdMemberDtoAlice = objectMapper.readValue(responseMemberAlice, MemberDto::class.java)
        val createdMemberDtoBob = objectMapper.readValue(responseMemberBob, MemberDto::class.java)

        assertEquals(2, createdMemberDtoBob.id)

        // AB transaction
        val participantBobDto = TransactionParticipantDto(id = createdMemberDtoBob.id, percentage = 100.0)
        val transactionAB = TransactionDto(
            type = Transaction.TransactionType.PAYMENT,
            amount = 100.0,
            paidById = createdMemberDtoAlice.id!!,
            participants = listOf(participantBobDto)
        )

        mockMvc.post("/api/transactions") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(transactionAB)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn()

        mockMvc.delete("/api/members/${createdMemberDtoAlice.id}") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn()
    }
}
