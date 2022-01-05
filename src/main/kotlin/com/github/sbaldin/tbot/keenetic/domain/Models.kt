package com.github.sbaldin.tbot.keenetic.domain

import kotlinx.serialization.Serializable

data class KeeneticAuthHeaderValues(
    val map: Map<KeeneticAuthHeaderEnum, String>
) {
    val xndmChallenge: String by lazy { map.getValue(KeeneticAuthHeaderEnum.XNDMChallenge) }
    val xndmRealm: String by lazy { map.getValue(KeeneticAuthHeaderEnum.XNDMRealm) }
}

@Serializable
data class KeeneticInterfaceInfo(val id: String, val type: String, val address: String)

@Serializable
data class UserCredentials(val login: String, val password: String)

