/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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


public class BigInt(
    override val mag: List<Int>,
    override val sigNum: BigSigned
): BigMath<List<Int>>, MathLogic, ExcHelper {

    override val bitCount: Int by lazy { bitCount(mag, sigNum) }
    override val bitLength: Int by lazy { bitLength(mag, sigNum) }
    override val firstNonZero: Int by lazy { firstNonZero(mag) }

    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        return equalsCompare(other)
    }

    public override fun hashCode(): Int {
        var result = mag.hashCode()
        result = 31 * result + sigNum.hashCode()
        return result
    }

    public fun toInt(): Int = toInt(mag, sigNum, firstNonZero)

    public fun toLong(): Long = toLong(mag, sigNum, firstNonZero)

    public fun asMutableBigInt(): MutableBigInt = MutableBigInt(mag.toMutableList(), sigNum)
    public override fun toBigInt(): BigInt = this

    public fun ofIntArray(value: IntArray): BigInt = fromIntArray(value) { m, s -> BigInt(m.toList(), s ) }
    public fun ofLong(value: Long): BigInt = fromLong(value) { m, s -> BigInt(m.toList(), s) }

    public companion object: MathLogic {
        public val minusOne: BigInt by lazy { fromLong(-1) { m, s -> BigInt(m.toList(), s ) } }
        public val zero: BigInt by lazy { fromLong(0) { m, s -> BigInt(m.toList(), s ) } }
        public val one: BigInt by lazy { fromLong(1) { m, s -> BigInt(m.toList(), s ) } }
        public val two: BigInt by lazy { fromLong(2) { m, s -> BigInt(m.toList(), s ) } }
    }
}


public fun BigInt.isNull(): Boolean = NullObject.bigInt === this

private val nullBigInt = BigInt(NullObject.intArray.toList(), BigSigned.ZERO)
public val NullObject.bigInt: BigInt
    get() = nullBigInt



public fun bigIntOf(value: ByteArray): BigInt = withLogic { fromByteArray(value) { m, s -> BigInt(m.toList(), s ) } }

public fun bigIntOf(value: Binary): BigInt = withLogic { fromBinary(value) { m, s -> BigInt(m.toList(), s ) } }

public fun bigIntOf(value: Long): BigInt = withLogic { fromLong(value) { m, s -> BigInt(m.toList(), s ) } }


public fun unsignedBigIntOf(value: ByteArray): BigInt = withLogic {
    val sanitized = stripLeadingZeros(value)
    return@withLogic BigInt(sanitized.toList(), sigNumZeroAdjust(sanitized, BigSigned.POSITIVE))
}

public fun unsignedBigIntOf(value: Binary): BigInt = withLogic {
    val sanitized = stripLeadingZeros(value)
    return@withLogic BigInt(sanitized.toList(), sigNumZeroAdjust(sanitized, BigSigned.POSITIVE))
}

public fun unsignedBigIntOf(value: String): BigInt = withLogic {
    val bytes = BinHex.decodeToBin(if(value.length.floorMod(2) == 1) "0$value" else value)
    val sanitized = stripLeadingZeros(bytes)
    return@withLogic BigInt(sanitized.toList(), sigNumZeroAdjust(sanitized, BigSigned.POSITIVE))
}

public fun BigInt.Companion.emptyBigIntOf(value: IntArray = intArrayOf(0)): BigInt = BigInt(
    value.toList(), BigSigned.POSITIVE)

public fun BigInt.Companion.ofIntArray(value: IntArray): BigInt = withLogic {
    fromIntArray(value) { m, s -> BigInt(m.toList(), s ) }
}

public fun BigInt.Companion.ofLong(value: Long): BigInt = withLogic {
    fromLong(value) { m, s -> BigInt(m.toList(), s) }
}