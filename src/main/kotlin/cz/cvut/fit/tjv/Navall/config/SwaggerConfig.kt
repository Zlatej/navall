package cz.cvut.fit.tjv.Navall.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI().info(
            Info()
                .title("Navall API")
                .version("v1.0.0")
                .description("API documentation for Navall app.")
        )
    }
}