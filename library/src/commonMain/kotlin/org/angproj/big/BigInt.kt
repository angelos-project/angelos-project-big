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

import org.angproj.big.newbig.ExportImportBigInt
import org.angproj.big.newbig.LoadAndSaveBigInt
import org.angproj.big.newbig.Unsigned
import org.angproj.big.newbig.bitCount
import org.angproj.big.newbig.bitLength
import org.angproj.big.newbig.firstNonzero


public data class BigInt internal constructor(
    val mag: IntArray,
    val sigNum: BigSigned
): ExcHelper {

    val bitCount: Int by lazy { bitCount(mag, sigNum) }
    val bitLength: Int by lazy { bitLength(mag, sigNum) }
    val firstNonZero: Int by lazy { mag.firstNonzero() }

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

    public fun isNull(): Boolean = nullObject === this

    public companion object: ExcHelper {
        public val minusOne: BigInt by lazy { ExportImportBigInt.valueOf(-1) }
        public val zero: BigInt by lazy { BigInt(intArrayOf(), BigSigned.ZERO) }
        public val one: BigInt by lazy { ExportImportBigInt.valueOf(1) }
        public val two: BigInt by lazy { ExportImportBigInt.valueOf(2) }

        public val nullObject: BigInt by lazy { BigInt(intArrayOf(), BigSigned.ZERO) }

        internal inline fun <reified R: Any> raw(
            mag: IntArray, sigNum: BigSigned
        ): BigInt = BigInt(mag, sigNum)//.also { MathLogicContext.register(it) }
    }
}

public fun BigInt.getByteSize(): Int = bitLength(mag, sigNum) / 8 + 1

public fun bigIntOf(value: ByteArray): BigInt = LoadAndSaveBigInt.internalOf(value)
public fun bigIntOf(value: Long): BigInt = ExportImportBigInt.valueOf(value)

public fun unsignedBigIntOf(value: ByteArray): BigInt = Unsigned.internalOf(value)

public fun biggerFirst(
    x: BigInt, y: BigInt, block: (x: BigInt, y: BigInt) -> BigInt
): BigInt = when (x.mag.size < y.mag.size) {
    true -> block(y, x)
    else -> block(x, y)
}