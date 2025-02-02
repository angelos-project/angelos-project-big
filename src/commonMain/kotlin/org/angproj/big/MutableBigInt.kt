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


public class MutableBigInt(
    override val mag: MutableList<Int>,
    override val sigNum: BigSigned
): BigMath<MutableList<Int>>, MathLogic, ExcHelper {

    override val bitCount: Int by lazy { bitCount(mag, sigNum) }
    override val bitLength: Int by lazy { bitLength(mag, sigNum) }
    override val firstNonZero: Int by lazy { firstNonZero(mag) }

    public override fun equals(other: Any?): Boolean {
        if(other == null) return false
        return equalsCompare(other)
    }

    public override fun hashCode(): Int {
        var result = mag.hashCode()
        result = 31 * result + sigNum.hashCode()
        return result
    }

    public override fun toBigInt(): BigInt = BigInt(mag.toList(), sigNum)

    public companion object
}


public fun MutableBigInt.Companion.emptyMutableBigIntOf(value: IntArray = intArrayOf(0)): MutableBigInt = MutableBigInt(
    value.toMutableList(), BigSigned.POSITIVE)

public fun MutableBigInt.Companion.ofIntArray(value: IntArray): MutableBigInt = withLogic {
    fromIntArray(value) { m, s -> MutableBigInt(m.toMutableList(), s ) }
}

public fun MutableBigInt.Companion.ofLong(value: Long): MutableBigInt = withLogic {
    fromLong(value) { m, s -> MutableBigInt(m.toMutableList(), s) }
}