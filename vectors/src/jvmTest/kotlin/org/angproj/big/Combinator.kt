/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits
import org.angproj.aux.sec.SecureRandom
import kotlin.math.absoluteValue


object Combinator {

    fun numberGenerator(range: IntRange, action: (num: ByteArray) -> Unit) {
        range.forEach {
            var arr = BigInt.createRandomBigInt(it.absoluteValue * TypeBits.byte)
            arr = if(it < 0) arr.negate() else arr
            action(arr.toByteArray())
        }
    }

    fun innerNumberGenerator(range: IntRange, action: (num: ByteArray) -> Unit) {
        range.forEach {
            var arr = BigInt.createRandomBigInt(it.absoluteValue * TypeBits.byte)
            arr = if(it < 0) arr.negate() else arr
            action(arr.toByteArray())
        }
    }

    fun intGenerator(range: IntRange, action: (num: Int) -> Unit) {
        range.forEach { action(SecureRandom.readInt()) }
    }

    fun longGenerator(range: IntRange, action: (num: Long) -> Unit) {
        range.forEach { action(SecureRandom.readLong()) }
    }
}