package cz.cvut.fit.tjv.Navall.controller

import com.fasterxml.jackson.databind.ObjectMapper
import cz.cvut.fit.tjv.Navall.models.Group
import cz.cvut.fit.tjv.Navall.models.dtos.GroupDto
import cz.cvut.fit.tjv.Navall.models.dtos.toDto
import cz.cvut.fit.tjv.Navall.service.GroupService
import io.mockk.every
import io.mockk.mockk
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Ensures H2 is used
@Transactional  // Rollback changes after each test
class GroupControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

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
    fun `create new group and get should return one group`() {
        val groupDto = GroupDto(name = "foo", currency = "bar")
        val response = mockMvc.post("/api/groups") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(groupDto)
        } .andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }
            .andReturn()
        .response
        .contentAsString

        val createdGroupDto = objectMapper.readValue(response, GroupDto::class.java)
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
}