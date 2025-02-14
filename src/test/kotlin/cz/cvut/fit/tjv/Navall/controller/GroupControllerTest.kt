package cz.cvut.fit.tjv.Navall.controller

import com.fasterxml.jackson.databind.ObjectMapper
import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Ensures H2 is used
@Transactional  // Rollback changes after each test
class GroupControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return empty list when no groups exist`() {
        mockMvc.get("/api/groups")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
            .andReturn()
            .response
            .let { response ->
                val body = response.contentAsString
                assert(body == "[]")
            }
    }

    @Test
    fun `create new group and get should return the group`() {
        val groupDto = GroupDto(name = "foo", currency = "bar")
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
        assertEquals(groupDto.name, createdGroupDto.name)
        assertEquals(groupDto.currency, createdGroupDto.currency)

        val response2 = mockMvc.get("/api/groups/${createdGroupDto.id}")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
            .andReturn()
            .response
            .contentAsString

        val getGroupDto = objectMapper.readValue(response2, GroupDto::class.java)

        assertEquals(createdGroupDto, getGroupDto)
    }

    @Test
    fun `create new group and delete should return the group`() {
        val groupDto = GroupDto(name = "foo", currency = "bar")
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
        assertEquals(groupDto.name, createdGroupDto.name)
        assertEquals(groupDto.currency, createdGroupDto.currency)

        val deleteResponse = mockMvc.delete("/api/groups/${createdGroupDto.id}")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
            .andReturn()
            .response
            .contentAsString

        val deletedGroupDto = objectMapper.readValue(deleteResponse, GroupDto::class.java)
        assertEquals(createdGroupDto, deletedGroupDto)
    }


    @Test
    fun `create update and delete and should not exist`() {
        // CREATE
        val groupDto = GroupDto(name = "foo", currency = "bar")
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
        assertEquals(groupDto.name, createdGroupDto.name)
        assertEquals(groupDto.currency, createdGroupDto.currency)

        // UPDATE

        val putGroupDto = GroupDto(name = "up", currency = "dated")
        val updateResponse = mockMvc.put("/api/groups/${createdGroupDto.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(putGroupDto)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
            .andReturn()
            .response
            .contentAsString

        val updatedGroupDto = objectMapper.readValue(updateResponse, GroupDto::class.java)

        // DELETE
        val deleteResponse = mockMvc.delete("/api/groups/${createdGroupDto.id}")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
            .andReturn()
            .response
            .contentAsString

        val deletedGroupDto = objectMapper.readValue(deleteResponse, GroupDto::class.java)
        assertEquals(updatedGroupDto, deletedGroupDto)
        assertNotEquals(deletedGroupDto, createdGroupDto)

        // NO LONGER EXISTS
        mockMvc.get("/api/groups/${createdGroupDto.id}")
            .andExpect {
                status { isNotFound() }
            }
    }
}