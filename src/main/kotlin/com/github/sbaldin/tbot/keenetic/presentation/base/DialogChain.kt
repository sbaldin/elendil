package com.github.sbaldin.tbot.keenetic.presentation.base

import com.elbekD.bot.Bot
import com.elbekD.bot.feature.chain.ChainBuilder

interface DialogChain {

    fun chain(bot: Bot): ChainBuilder

}
