package cz.cvut.fit.tjv.Navall

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

private var log = KotlinLogging.logger {}

@SpringBootApplication
class NavallApplication

fun main(args: Array<String>) {
    runApplication<NavallApplication>(*args)
    log.info { "App is running, now NavalLove! <3" }
}
