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
package org.angproj.big.oldbig


public class BigInt(
    override val mag: List<Int>,
    override val sigNum: BigSigned
): BigMath<List<Int>> {

    override val bitCount: Int by lazy { BigMath.bitCount(mag, sigNum) }
    override val bitLength: Int by lazy { BigMath.bitLength(mag, sigNum) }
    override val firstNonZero: Int by lazy { BigMath.firstNonZero(mag) }

    public override fun equals(other: Any?): Boolean {
        if(other == null) return false
        return equalsCompare(other)
    }

    public fun toInt(): Int = this.getIdx(0)

    public fun toLong(): Long = (this.getIdxL(1) shl 32) or this.getIdxL(0)

    public override fun asMutableBigInt(): MutableBigInt = MutableBigInt(mag.toMutableList(), sigNum)
    override fun toBigInt(): BigInt = this

    public fun ofIntArray(value: IntArray): BigInt = BigMath.fromIntArray(value) { m, s -> BigInt(m.toList(), s ) }
    public fun ofLong(value: Long): BigInt = BigMath.fromLong(value) { m, s -> BigInt(m.toList(), s) }

    /**
     * TODO(Investigate minusOne in all debugging and comparison testing immediately)
     * */
    public companion object {
        public val minusOne: BigInt by lazy { BigMath.fromLong(-1) { m, s -> BigInt(m.toList(), s ) } }
        public val zero: BigInt by lazy { BigMath.fromLong(0) { m, s -> BigInt(m.toList(), s ) } }
        public val one: BigInt by lazy { BigMath.fromLong(1) { m, s -> BigInt(m.toList(), s ) } }
        public val two: BigInt by lazy { BigMath.fromLong(2) { m, s -> BigInt(m.toList(), s ) } }
    }
}

public fun BigInt.isNull(): Boolean = NullObject.bigInt === this

private val nullBigInt = BigInt(emptyList(), BigSigned.ZERO)
public val NullObject.bigInt: BigInt
    get() = nullBigInt

public fun bigIntOf(value: ByteArray): BigInt = BigMath.fromByteArray(value) { m, s -> BigInt(m.toList(), s ) }

public fun bigIntOf(value: Long): BigInt = BigMath.fromLong(value) { m, s -> BigInt(m.toList(), s ) }

public fun unsignedBigIntOf(value: ByteArray): BigInt {
    val sanitized = BigMath.stripLeadingZeros(value)
    return BigInt(sanitized.toList(), BigMath.sigNumZeroAdjust(sanitized, BigSigned.POSITIVE))
}
