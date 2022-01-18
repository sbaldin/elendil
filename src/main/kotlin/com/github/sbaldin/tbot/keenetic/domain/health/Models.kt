package com.github.sbaldin.tbot.keenetic.domain.health

class HealthStatus(
    val service: String,
    val status: HealthStatusEnum,
)

enum class HealthStatusEnum {
    ALIVE, DEAD
}
