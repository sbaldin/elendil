package com.github.sbaldin.tbot.keenetic

import com.github.sbaldin.tbot.keenetic.domain.gateway.KeeneticAuthHeaderEnum
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.security.MessageDigest
import java.util.*


fun Headers.asMap(): EnumMap<KeeneticAuthHeaderEnum, String> = EnumMap(
    entries().asSequence()
        .filter { it -> it.key in KeeneticAuthHeaderEnum.values().map { it.title } }
        .associate { header -> KeeneticAuthHeaderEnum.from(header.key) to header.value.first() }
)

// However, here we have to use a custom byte to hex converter to get the hashed value in hexadecimal
private fun printHexBinary(hash: ByteArray): String {
    val hexString = StringBuilder(2 * hash.size)
    for (i in hash.indices) {
        val hex = Integer.toHexString(0xff and hash[i].toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString()
}

fun String.encodeMd5(): String {
    return printHexBinary(MessageDigest.getInstance("MD5").digest(toByteArray()))
}

// see more https://www.baeldung.com/sha-256-hashing-java
fun String.encodeSha256(): String {
// Java provides inbuilt MessageDigest class for SHA-256 hashing
    return printHexBinary(MessageDigest.getInstance("SHA-256").digest(toByteArray()))
}

suspend fun <A, R> Iterable<A>.mapParallel(f: suspend (A) -> R): Iterable<R> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}