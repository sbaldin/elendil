package com.github.sbaldin.tbot.keenetic

import com.github.sbaldin.tbot.keenetic.config.BotConf
import com.github.sbaldin.tbot.keenetic.config.HealthCheckConf
import com.github.sbaldin.tbot.keenetic.config.UserCredentialConf
import com.github.sbaldin.tbot.keenetic.domain.CredentialsProvider
import com.github.sbaldin.tbot.keenetic.domain.UserCredentials
import com.github.sbaldin.tbot.keenetic.domain.gateway.GatewayResolver
import com.github.sbaldin.tbot.keenetic.domain.health.HealthChecker
import com.github.sbaldin.tbot.keenetic.presentation.GatewayLocatorBot
import com.github.sbaldin.tbot.keenetic.presentation.LocateGatewayChainPresenter
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger(Application::class.java)

fun readBotConf(
    resourcePath: String = "application-bot.yaml",
    botConfPath: String = "",
) = Config()
    .from.yaml.resource(resourcePath).from.yaml.file(botConfPath, optional = true).at("bot").toValue<BotConf>()

fun readCredentialsConf(
    resourcePath: String = "application-bot.yaml",
    botConfPath: String = "",
) = Config()
    .from.yaml.resource(resourcePath).from.yaml.file(botConfPath, optional = true).at("credentials")
    .toValue<UserCredentialConf>()

fun healthCheckConf(
    resourcePath: String = "application-bot.yaml",
    botConfPath: String = "",
) = Config()
    .from.yaml.resource(resourcePath).from.yaml.file(botConfPath, optional = true).at("health_checker")
    .toValue<HealthCheckConf>()


class Application {
    private val appConfPath: String = System.getProperty("appConfig") ?: "./application-bot.yaml"
    private val appConf: BotConf = readBotConf(botConfPath = appConfPath)
    private val healthCheckConf: HealthCheckConf = healthCheckConf(botConfPath = appConfPath)

    fun run() {
        log.info("Application config path:$appConfPath")
        val locale = appConf.locale()
        log.info("Application locale path:$locale")
        val dialogs = listOf(
            LocateGatewayChainPresenter(
                appConf.authorizedChatId,
                GatewayResolver(),
                HealthChecker(healthCheckConf.services),
                object : CredentialsProvider {
                    override fun get(): UserCredentials {
                        val userConf: UserCredentialConf = readCredentialsConf(botConfPath = appConfPath)
                        return UserCredentials(userConf.login, userConf.password)
                    }
                }
            )
        )
        GatewayLocatorBot(
            appConf.name,
            appConf.token,
            dialogs,
        ).start()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val app = Application()
            log.info("Starting Telegram Cyber Anny Bot.")
            app.run()
            log.info("Bot connected to telegram api.")
        }
    }
}