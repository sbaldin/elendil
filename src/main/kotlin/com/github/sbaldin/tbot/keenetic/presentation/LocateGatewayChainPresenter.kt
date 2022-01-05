package com.github.sbaldin.tbot.keenetic.presentation

import com.elbekD.bot.Bot
import com.elbekD.bot.feature.chain.ChainBuilder
import com.elbekD.bot.feature.chain.chain
import com.elbekD.bot.feature.chain.terminateChain
import com.elbekD.bot.types.Message
import com.github.sbaldin.tbot.keenetic.domain.CredentialsProvider
import com.github.sbaldin.tbot.keenetic.domain.GatewayResolver
import com.github.sbaldin.tbot.keenetic.presentation.base.DialogChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LocateGatewayChainPresenter(
    private val authorizedChatId: Long,
    private val gatewayResolver: GatewayResolver,
    private val credentialsProvider: CredentialsProvider
) : DialogChain {

    override fun chain(bot: Bot): ChainBuilder = bot.chain("/start") { msg ->
        val gatewayMessage = if (msg.chat.id == authorizedChatId) {
            "Here is your gateway" + gatewayResolver.getIspInfo(credentialsProvider.get())
        } else {
            "Oops! You are not allowed to get gateway, sorry!"
        }

        bot.sendMessage(
            msg.chat.id,
            createGreetingMsg(msg) + "\n" + gatewayMessage
        )
        bot.terminateChain(msg.chat.id)
    }

    private fun createGreetingMsg(msg: Message): String {
        return msg.from?.let { "Hello, ${it.first_name}!" } ?: "!"
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(LocateGatewayChainPresenter::class.java)
    }
}