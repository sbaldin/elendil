package com.github.sbaldin.tbot.keenetic.config

import com.github.sbaldin.tbot.keenetic.domain.UserCredentials
import java.util.Locale

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
