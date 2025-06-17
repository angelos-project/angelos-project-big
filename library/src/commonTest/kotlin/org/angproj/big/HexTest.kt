package org.angproj.big

import kotlin.test.Test

class HexTest {
    @Test
    fun fixTrix() {
        (0 until 256).forEach {
            val apa = BigInt.createRandomBigInt(it)
            println(apa.toHexString())
        }
    }
}