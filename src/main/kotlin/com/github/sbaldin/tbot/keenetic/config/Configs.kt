package com.github.sbaldin.tbot.keenetic.config

import java.util.*

data class BotConf(
    val name: String,
    val locale: String,
    val token: String,
    val authorizedChatId: Long,
) {
    fun locale(): Locale = Locale(locale.lowercase(), locale.uppercase())
}

data class UserCredentialConf(
    val login: String,
    val password: String
)

data class HealthCheckConf(
    val services: List<String>,
)
