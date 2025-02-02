package org.angproj.big

import org.angproj.aux.io.Binary
import org.angproj.aux.io.toBinary
import org.angproj.aux.io.toByteArray
import org.angproj.aux.sec.SecureRandom
import kotlin.math.absoluteValue


object Combinator {

    fun numberGenerator(range: IntRange, action: (num: ByteArray) -> Unit) {
        range.forEach {
            val arr = BigInt.random(it.absoluteValue, if(it < 0) BigSigned.NEGATIVE else BigSigned.POSITIVE)
            action(arr)
        }
    }

    fun innerNumberGenerator(range: IntRange, action: (num: ByteArray) -> Unit) {
        range.forEach {
            val arr = BigInt.random(it.absoluteValue, if(it < 0) BigSigned.NEGATIVE else BigSigned.POSITIVE)
            action(arr)
        }
    }

    fun intGenerator(range: IntRange, action: (num: Int) -> Unit) {
        range.forEach {
            action(SecureRandom.readInt())
        }
    }

    fun longGenerator(range: IntRange, action: (num: Long) -> Unit) {
        range.forEach {
            action(SecureRandom.readLong())
        }
    }
}