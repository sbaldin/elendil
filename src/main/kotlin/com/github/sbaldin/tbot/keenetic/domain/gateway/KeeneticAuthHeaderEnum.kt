package com.github.sbaldin.tbot.keenetic.domain.gateway

enum class KeeneticAuthHeaderEnum(val title: String) {
    XNDMChallenge("X-NDM-Challenge"),
    XNDMRealm("X-NDM-Realm");

    companion object {
        fun from(title: String) = values().first { it.title == title }
    }
}