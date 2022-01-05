package com.github.sbaldin.tbot.keenetic.domain

import com.github.sbaldin.tbot.keenetic.asMap
import com.github.sbaldin.tbot.keenetic.encodeMd5
import com.github.sbaldin.tbot.keenetic.encodeSha256
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class GatewayResolver {

    fun getIspInfo(cred: UserCredentials) = runBlocking {
        // HttpClientEngineFactory using a Coroutine based I/O implementation
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            //log all requests
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(HttpCookies) {
                // Will keep an in-memory map with all the cookies from previous requests.
                storage = AcceptAllCookiesStorage()
            }
        }
        // try with resource kotlin way
        val interfaceInfo = client.use {
            // do auth
            authorized(client, cred) {
                // here we have authorized
                client.get<KeeneticInterfaceInfo>("http://192.168.1.1/rci/show/interface?name=ISP")
            }

        }
        interfaceInfo
    }

    private suspend fun <T> authorized(client: HttpClient, userCredentials: UserCredentials, body: suspend () -> T): T {
        val unauthorizedResponse = client.get<HttpResponse>("http://192.168.1.1/auth") { expectSuccess = false }
        return if (unauthorizedResponse.status == HttpStatusCode.Unauthorized) {
            val tokenAndRealm = KeeneticAuthHeaderValues(unauthorizedResponse.headers.asMap())
            val authPostResponse = authRequest(client, tokenAndRealm, userCredentials)
            if (authPostResponse.status == HttpStatusCode.OK) {
                body()
            } else {
                throw IllegalArgumentException("Auth request has been failed with args: ${userCredentials.login}!")
            }
        } else if (unauthorizedResponse.status == HttpStatusCode.OK) {
            body()
        } else {
            throw IllegalStateException("Unknown response: $unauthorizedResponse")
        }
    }

    private suspend fun authRequest(
        client: HttpClient,
        tokenAndRealm: KeeneticAuthHeaderValues,
        nonEncryptedUserCredentials: UserCredentials
    ): HttpResponse {
        val login = nonEncryptedUserCredentials.login
        val password = nonEncryptedUserCredentials.password
        val authPostResponse = client.post<HttpResponse>("http://192.168.1.1/auth") {
            contentType(ContentType.Application.Json)
            expectSuccess = false
            val md5 = (login + ":" + tokenAndRealm.xndmRealm + ':' + password).encodeMd5()
            body = UserCredentials(
                login = login,
                password = (tokenAndRealm.xndmChallenge + md5).encodeSha256()
            )
        }
        return authPostResponse
    }
}