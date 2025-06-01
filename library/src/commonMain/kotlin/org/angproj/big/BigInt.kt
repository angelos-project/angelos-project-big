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
 * Acknowledgement of algorithm:
 *      Per Bothner
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

import org.angproj.aux.util.NullObject
import org.angproj.big.newbig.ExportImportBigInt
import org.angproj.big.newbig.LoadAndSaveBigInt


public data class BigInt internal constructor(
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
        var result = mag.contentHashCode()
        result = 31 * result + sigNum.hashCode()
        return result
    }

    public fun toInt(): Int = ExportImportBigInt.intValue(mag, sigNum)

    public fun toLong(): Long = ExportImportBigInt.longValue(mag, sigNum)

    public fun toByteArray(): ByteArray = LoadAndSaveBigInt.toByteArrayNew(mag, sigNum)
    //public fun toByteArray(): ByteArray = toByteArray(mag, sigNum, firstNonZero)


    public companion object: MathLogic {
        public val minusOne: BigInt by lazy { fromLong(-1) }
        public val zero: BigInt by lazy { fromLong(0) }
        public val one: BigInt by lazy { fromLong(1) }
        public val two: BigInt by lazy { fromLong(2) }

        internal inline fun <reified R: Any> raw(
            mag: IntArray, sigNum: BigSigned
        ): BigInt = BigInt(mag, sigNum)//.also { MathLogicContext.register(it) }
    }
}


public fun BigInt.isNull(): Boolean = NullObject.bigInt === this
private val nullBigInt = BigInt.raw<Unit>(NullObject.intArray, BigSigned.ZERO)
public val NullObject.bigInt: BigInt
    get() = nullBigInt
