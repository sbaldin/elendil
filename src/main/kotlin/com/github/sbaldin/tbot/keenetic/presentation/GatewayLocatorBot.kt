package com.github.sbaldin.tbot.keenetic.presentation

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.elbekD.bot.types.PhotoSize
import com.github.sbaldin.tbot.keenetic.presentation.base.DialogChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class GatewayLocatorBot(
    private val botName: String,
    private val token: String,
    private val dialogs: List<DialogChain>,
) {

    fun start() {
        log.info("Bot Initialize.")
        val bot = Bot.createPolling(botName, token) {
            limit = 50
            timeout = 30
            removeWebhookAutomatically = true
            period = 1000
        }
        addLogFilter(bot)
        buildChains(bot)
        log.info("Bot Initialization finished.")
        bot.start()
        log.info("Bot has been started.")
    }

    private fun addLogFilter(bot: Bot) {
        bot.onMessage { msg ->
            log.info("msg:${msg.toLightMessageModel()}")
        }
    }

    private fun buildChains(bot: Bot) {
        dialogs.forEach {
            log.info("Build ${it::class.java.name} chain.")
            it.chain(bot).build()
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(GatewayLocatorBot::class.java)
    }
}

private data class LightMessageModel(
    val message_id: Int,
    val from: String?,
    val sender_chat: String?,
    val date: Int,
    val text: String?,
    val photo: PhotoSize?,
)

private fun Message.toLightMessageModel(): LightMessageModel {
    return LightMessageModel(
        message_id = this.message_id,
        from = this.from?.username + this.from?.id,
        sender_chat = this.sender_chat?.title?: this.chat.id.toString(),
        date = this.date.also { Date(it * 100L) },
        text = this.text,
        photo = this.photo?.maxByOrNull { it.file_size },
    )
}