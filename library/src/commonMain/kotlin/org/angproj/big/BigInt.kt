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

/**
 * Arbitrary precision integer.
 *
 * The magnitude is represented as an array of integers in big-endian order, where each integer
 * represents 32 bits of the number. The most significant integer is at index 0.
 *
 * The sign is represented by the [sigNum] property, which can be positive, negative, or zero.
 *
 * @property mag The magnitude of the BigInt as an array of integers.
 * @property sigNum The sign of the BigInt.
 */
public data class BigInt(
    public val mag: IntArray,
    public val sigNum: BigSigned
): Comparable<BigInt> {

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

    /**
     * Compares this BigInt with another BigInt.
     *
     * @param other the BigInt to compare with.
     * @return a negative integer, zero, or a positive integer as this BigInt is less than, equal to, or greater than the specified BigInt.
     */
    override fun compareTo(other: BigInt): Int = compareSpecial(other).state

    override fun hashCode(): Int {
        // Truth is, different corporate implementations may have different hash algos.
        var hash = 0
        for (element in mag) {
            hash = 31 * hash + (element.toLong() and 0xffffffffL).toInt()
        }
        return hash * sigNum.state
    }

    /**
     * Checks if this BigInt is the null object representation.
     *
     * @return true if this BigInt represents the null object, false otherwise.
     */
    public fun isNull(): Boolean = nullObject === this

    public companion object {
        public val zero: BigInt by lazy { bigIntOf(byteArrayOf(0)) }
        public val minusOne: BigInt by lazy { bigIntOf(byteArrayOf(-1)) }
        public val one: BigInt by lazy { bigIntOf(byteArrayOf(1)) }
        public val two: BigInt by lazy { bigIntOf(byteArrayOf(2)) }

        public val nullObject: BigInt by lazy { BigInt(intArrayOf(), BigSigned.ZERO) }
    }
}

/**
 * Converts the BigInt to an Int representation.
 *
 * @return An Int representing the value of the BigInt.
 */
public fun BigInt.toInt(): Int = ExportImportBigInt.intValue(mag, sigNum)

/**
 * Converts the BigInt to a Long representation.
 *
 * @return A Long representing the value of the BigInt.
 */
public fun BigInt.toLong(): Long = ExportImportBigInt.longValue(mag, sigNum)

public val BigInt.bitLength: Int
    get() = LoadAndSaveBigInt.bitLength(mag, sigNum)

public val BigInt.bitCount: Int
    get() = LoadAndSaveBigInt.bitCount(mag, sigNum)

/**
 * Converts the BigInt to a ByteArray representation.
 *
 * @return A ByteArray representing the value of the BigInt.
 */
public fun BigInt.toByteArray(): ByteArray = LoadAndSaveBigInt.toByteArray(mag, sigNum)

/**
 * Returns the number of bytes required to represent the BigInt.
 *
 * @return The number of bytes required to represent the BigInt.
 */
public fun BigInt.getByteSize(): Int = bitLength / 8 + 1

/**
 * Creates a BigInt from a ByteArray.
 *
 * @param value The ByteArray to convert to BigInt.
 * @return A BigInt representing the given ByteArray.
 */
public fun bigIntOf(value: ByteArray): BigInt = LoadAndSaveBigInt.internalOf(value)

/**
 * Creates a BigInt from a Long value.
 *
 * @param value The Long value to convert to BigInt.
 * @return A BigInt representing the given Long value.
 */
public fun bigIntOf(value: Long): BigInt = ExportImportBigInt.valueOf(value)

/**
 * Creates a BigInt from a ByteArray, treating the bytes as an unsigned value.
 *
 * @param value The ByteArray to convert to BigInt.
 * @return A BigInt representing the given ByteArray.
 */
public fun unsignedBigIntOf(value: ByteArray): BigInt = Unsigned.internalOf(value)