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

public data class BigInt(
    public val mag: IntArray,
    public val sigNum: BigSigned
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return equalsCompare(other)
    }

    public fun equalsCompare(x: Any): Boolean {
        if (x === this) return true
        if (x !is BigInt) return false
        if (sigNum != x.sigNum) return false
        if (mag.size != x.mag.size) return false
        return mag.indices.indexOfFirst { mag[it] != x.mag[it] } == -1
    }

    public override fun hashCode(): Int {
        var result = mag.contentHashCode()
        result = 31 * result + sigNum.hashCode()
        return result
    }

    public fun isNull(): Boolean = nullObject === this

    public companion object {
        public val zero: BigInt by lazy { bigIntOf(byteArrayOf(0)) }
        public val minusOne: BigInt by lazy { bigIntOf(byteArrayOf(-1)) }
        public val one: BigInt by lazy { bigIntOf(byteArrayOf(1)) }
        public val two: BigInt by lazy { bigIntOf(byteArrayOf(2)) }

        public val nullObject: BigInt by lazy { BigInt(intArrayOf(), BigSigned.ZERO) }
    }
}

public fun BigInt.toInt(): Int = ExportImportBigInt.intValue(mag, sigNum)

public fun BigInt.toLong(): Long = ExportImportBigInt.longValue(mag, sigNum)


public val BigInt.bitLength: Int
    get() = LoadAndSaveBigInt.bitLength(mag, sigNum)


public val BigInt.bitCount: Int
    get() = LoadAndSaveBigInt.bitCount(mag, sigNum)


public fun BigInt.toByteArray(): ByteArray = LoadAndSaveBigInt.toByteArray(mag, sigNum)

public fun BigInt.getByteSize(): Int = bitLength / 8 + 1

public fun bigIntOf(value: ByteArray): BigInt = LoadAndSaveBigInt.internalOf(value)
public fun bigIntOf(value: Long): BigInt = ExportImportBigInt.valueOf(value)

public fun unsignedBigIntOf(value: ByteArray): BigInt = Unsigned.internalOf(value)

public fun biggerFirst(
    x: BigInt, y: BigInt, block: (x: BigInt, y: BigInt) -> BigInt
): BigInt = when (x.mag.size < y.mag.size) {
    true -> block(y, x)
    else -> block(x, y)
}

/*internal inline fun<reified T: Throwable> ensureError(message: String = "BigInt error"): Nothing = throw BigMathException(message)

internal inline fun<reified T: Throwable> ensureThat(condition: Boolean, message: () -> String) {
    if (!condition) ensureError<T>(message())
}*/