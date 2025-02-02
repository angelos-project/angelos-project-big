/**
 * Copyright (c) 2023-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.aux.io.Binary
import org.angproj.aux.util.BinHex
import org.angproj.aux.util.NullObject
import org.angproj.aux.util.floorMod


public data class BigInt(
    val mag: IntArray,
    val sigNum: BigSigned
): MathLogic {

    val bitCount: Int by lazy { bitCount(mag, sigNum) }
    val bitLength: Int by lazy { bitLength(mag, sigNum) }
    val firstNonZero: Int by lazy { firstNonZero(mag) }

    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        return equalsCompare(other)
    }

    public fun equalsCompare(x: Any): Boolean {
        if(x === this) return true
        if(x !is BigInt) return false
        if(sigNum != x.sigNum) return false
        if(mag.size != x.mag.size) return false
        return mag.indices.indexOfFirst { mag[it] != x.mag[it] } == -1
    }

    public override fun hashCode(): Int {
        var result = mag.hashCode()
        result = 31 * result + sigNum.hashCode()
        return result
    }

    public fun toInt(): Int = toInt(mag, sigNum, firstNonZero)

    public fun toLong(): Long = toLong(mag, sigNum, firstNonZero)

    public fun toByteArray(): ByteArray = toByteArray(mag, sigNum, firstNonZero)

    public companion object: MathLogic {
        public val minusOne: BigInt by lazy { fromLong(-1) }
        public val zero: BigInt by lazy { fromLong(0) }
        public val one: BigInt by lazy { fromLong(1) }
        public val two: BigInt by lazy { fromLong(2) }
    }
}


public fun BigInt.isNull(): Boolean = NullObject.bigInt === this
private val nullBigInt = BigInt(NullObject.intArray, BigSigned.ZERO)
public val NullObject.bigInt: BigInt
    get() = nullBigInt


public fun bigIntOf(value: ByteArray): BigInt = withLogic { fromByteArray(value) }
public fun bigIntOf(value: Binary): BigInt = withLogic { fromBinary(value) }
public fun bigIntOf(value: Long): BigInt = withLogic { fromLong(value) }


public fun unsignedBigIntOf(value: ByteArray): BigInt = withLogic {
    val sanitized = stripLeadingZeros(value)
    return@withLogic BigInt(sanitized, sigNumZeroAdjust(sanitized, BigSigned.POSITIVE))
}

public fun unsignedBigIntOf(value: Binary): BigInt = withLogic {
    val sanitized = stripLeadingZeros(value)
    return@withLogic BigInt(sanitized, sigNumZeroAdjust(sanitized, BigSigned.POSITIVE))
}

public fun unsignedBigIntOf(value: String): BigInt = withLogic {
    val bytes = BinHex.decodeToBin(if(value.length.floorMod(2) == 1) "0$value" else value)
    val sanitized = stripLeadingZeros(bytes)
    return@withLogic BigInt(sanitized, sigNumZeroAdjust(sanitized, BigSigned.POSITIVE))
}