package com.github.sbaldin.tbot.keenetic

import org.spekframework.spek2.Spek

object EncodeSpeck : Spek({
    group("Encoding String with md5 and sha256") {
        val md5 = ("admin" + ":" + "Keenetic Ultra" + ':' + "admin").encodeMd5()
        test("encode string with md5 ") {
            assert("d96b73b3e428fa4fcd0f3a454b7519fe" == md5)
        }
        val token = "QWLZEFMNBQITTQKIPFWHKTTKFPUTKPUL"
        test("encode string with md5 ") {
            assert("0053245428fe1bf01105019396519b7887440840c53c4406a39514b57fdcd49a" == (token  + md5).encodeSha256())
        }
    }
})