/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.SecureFeed
import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ceilDiv
import org.angproj.sec.util.ensure
import org.angproj.sec.rand.JitterEntropy


internal fun BigInt.Companion.innerCreateBigint(bitLength: Int, random: (ByteArray) -> Unit): BigInt {
    ensure(bitLength >= 0) { BigMathException("Bit length must be greater than zero") }
    val randomBytes = ByteArray(bitLength.ceilDiv(TypeSize.byteBits)+4)

    random(randomBytes)
    val value = bigIntOf(randomBytes).abs()
    val valueBitLength = value.bitLength

    return when {
        valueBitLength == bitLength -> value
        valueBitLength > bitLength -> value.shiftRight(valueBitLength - bitLength)
        else -> ensure{ BigMathException("Random truly failed") }
    }
}

internal fun BigInt.Companion.innerCreateInRange(min: BigInt, max: BigInt, random: (ByteArray) -> Unit): BigInt {
    ensure(min < max) { BigMathException("Min is larger than max") }
    val diff = max.subtract(min)
    val diffBitLength = diff.bitLength
    return innerCreateBigint(diffBitLength, random).mod(diff).add(min)
}

/**
 * Creates a random BigInt with the specified bit length using jitter entropy.
 *
 * @param bitLength The desired bit length of the random BigInt.
 * @return A random BigInt with the specified bit length.
 * @throws BigMathException If the random generation fails.
 */
public fun BigInt.Companion.createEntropyBigInt(bitLength: Int): BigInt {
    return innerCreateBigint(bitLength) {
        JitterEntropy.readBytes(it, 0, it.size) { index, value -> it[index] = value }
    }
}

/**
 * Creates a random BigInt within the specified range [min, max) using jitter entropy.
 *
 * @param min The minimum value (inclusive).
 * @param max The maximum value (exclusive).
 * @return A random BigInt in the range [min, max).
 * @throws BigMathException If min is greater than or equal to max.
 */
public fun BigInt.Companion.createEntropyInRange(min: BigInt, max: BigInt): BigInt {
    return innerCreateInRange(min, max) {
        JitterEntropy.readBytes(it, 0, it.size) { index, value -> it[index] = value }
    }
}

/**
 * Creates a random BigInt with the specified bit length.
 *
 * @param bitLength The desired bit length of the random BigInt.
 * @return A random BigInt with the specified bit length.
 * @throws BigMathException If the random generation fails.
 */
public fun BigInt.Companion.createRandomBigInt(bitLength: Int): BigInt {
    return innerCreateBigint(bitLength) {
        SecureFeed.readBytes(it, 0, it.size) { index, value -> it[index] = value }
    }
}

/**
 * Creates a random BigInt within the specified range [min, max).
 *
 * @param min The minimum value (inclusive).
 * @param max The maximum value (exclusive).
 * @return A random BigInt in the range [min, max).
 * @throws BigMathException If min is greater than or equal to max.
 */
public fun BigInt.Companion.createRandomInRange(min: BigInt, max: BigInt): BigInt {
    return innerCreateInRange(min, max) {
        SecureFeed.readBytes(it, 0, it.size) { index, value -> it[index] = value }
    }
}