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

import org.angproj.aux.io.TypeBits
import org.angproj.aux.io.TypeSize
import org.angproj.aux.io.toByteArray
import org.angproj.aux.mem.BufMgr
import org.angproj.aux.sec.SecureRandom
import org.angproj.big.newbig.ExportImportBigInt
import org.angproj.big.newbig.bitLength

/**
 * Creates a random BigInt with the specified bit length.
 *
 * @param bitLength The desired bit length of the random BigInt.
 * @return A random BigInt with the specified bit length.
 * @throws BigMathException If the random generation fails.
 */
public fun BigInt.Companion.createRandomBigInt(bitLength: Int): BigInt {
    val random = BufMgr.bin(
        bitLength / TypeBits.int * TypeSize.int + (
                if(bitLength % TypeBits.int > 0) TypeSize.int else 0
                ) + TypeSize.int)
    SecureRandom.read(random)
    val value = bigIntOf(random.toByteArray()).abs()
    //val value = fromBinary(random).abs()
    val valueBitLength = value.bitLength()
    return when {
        valueBitLength == bitLength -> value
        valueBitLength > bitLength -> value.shiftRight(valueBitLength - bitLength)
        else -> error { BigMathException("Random truly failed") }
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
    require(min < max) { throw BigMathException("Min is larger than max") }
    val diff = max.subtract(min)
    val diffBitLength = diff.bitLength()
    return createRandomBigInt(diffBitLength).mod(diff).add(min)
}
