package com.github.sbaldin.tbot.keenetic.domain.health

import com.github.sbaldin.tbot.keenetic.domain.health.HealthStatusEnum.ALIVE
import com.github.sbaldin.tbot.keenetic.domain.health.HealthStatusEnum.DEAD
import com.github.sbaldin.tbot.keenetic.mapParallel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HealthChecker(val serviceEndpoints: List<String>) {

    fun health(): List<HealthStatus> = runCatching {
        val client = HttpClient(CIO) {
            install(JsonFeature)
            install(HttpTimeout) {
                requestTimeoutMillis = 2000
            }
            expectSuccess = false
            //log all requests
            /*install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }*/
        }
        // try with resource kotlin way
        val healthStatuses = client.use {
            // here we have authorized
            runBlocking {
                serviceEndpoints.mapParallel { serviceUrl ->
                    val response = client.get<HttpResponse>(serviceUrl)
                    val status = if (response.status == HttpStatusCode.OK) ALIVE else DEAD
                    HealthStatus(serviceUrl, status)
                }
            }

        }
        healthStatuses.toList()
    }.getOrElse {
        log.error("Couldn't execute health check due to exception.", it)
        emptyList()
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(HealthChecker::class.java)
    }
}