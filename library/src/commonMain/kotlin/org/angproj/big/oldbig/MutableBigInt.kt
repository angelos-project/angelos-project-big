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


public class MutableBigInt(
    override val mag: MutableList<Int>,
    override val sigNum: BigSigned
): BigMath<MutableList<Int>> {

    override val bitCount: Int by lazy { BigMath.bitCount(mag, sigNum) }
    override val bitLength: Int by lazy { BigMath.bitLength(mag, sigNum) }
    override val firstNonZero: Int by lazy { BigMath.firstNonZero(mag) }

    public override fun equals(other: Any?): Boolean {
        if(other == null) return false
        return equalsCompare(other)
    }

    public fun <E : MutableList<Int>> E.revSet(index: Int, value: Int) {
        this[lastIndex - index] = value
    }

    public fun setIdx(index: Int, num: Int): Unit = mag.revSet(index, num)

    public fun setIdxL(index: Int, num: Long): Unit = setIdx(index, num.toInt())

    public fun setUnreversedIdx(index: Int, num: Int) { mag[index] = num }

    public fun setUnreversedIdxL(index: Int, num: Long): Unit = setUnreversedIdx(index, num.toInt())

    public fun toComplementedIntArray(): IntArray = IntArray(mag.size) { getUnreversedIdx(it) }

    public override fun asMutableBigInt(): MutableBigInt = this
    override fun toBigInt(): BigInt = BigInt(mag.toList(), sigNum)

    public companion object: BigScope {
        public fun emptyMutableBigIntOf(value: IntArray = intArrayOf(0)): MutableBigInt = MutableBigInt(
            value.toMutableList(), BigSigned.POSITIVE)

        public fun ofIntArray(value: IntArray): MutableBigInt = BigMath.fromIntArray(value) { m, s ->
            MutableBigInt(m.toMutableList(), s ) }

        public fun ofLong(value: Long): MutableBigInt = BigMath.fromLong(value) { m, s ->
            MutableBigInt(m.toMutableList(), s) }
    }
}