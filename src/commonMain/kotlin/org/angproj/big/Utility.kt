/**
 * Copyright (c) 2023 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.aux.io.binOf
import org.angproj.aux.io.toByteArray
import org.angproj.aux.sec.SecureRandom


public fun BigInt.Companion.between(start: BigInt, end: BigInt): BigInt {
    val random = binOf(end.bitLength / 8 + 1)
    SecureRandom.read(random)
    val value = fromByteArray(random.toByteArray()) { m, s -> BigInt(m.toList(), s ) }
    return value.shiftRight(value.bitLength - end.bitLength + 1).abs().add(start)
}

public fun BigInt.Companion.random(size: Int, sigNum: BigSigned = BigSigned.ZERO): ByteArray {
    val random = binOf(size + 1)
    SecureRandom.read(random)
    random.storeByte(0, sigNum.signed.toByte())
    return random.toByteArray()
}