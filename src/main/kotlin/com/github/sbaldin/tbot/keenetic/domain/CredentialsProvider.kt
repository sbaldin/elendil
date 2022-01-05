package com.github.sbaldin.tbot.keenetic.domain

interface CredentialsProvider{
    fun get(): UserCredentials
}