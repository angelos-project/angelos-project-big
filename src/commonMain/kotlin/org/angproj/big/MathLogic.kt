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
import org.angproj.aux.io.TypeBits
import org.angproj.aux.io.TypeSize
import org.angproj.aux.util.BufferAware
import org.angproj.aux.util.swapEndian
import kotlin.math.max


public interface MathLogic: BufferAware, ExcHelper {

    public fun intSize(big: BigInt): Int = intSize(big.bitLength)
    public fun getByteSize(big: BigInt): Int = getByteSize(big.mag, big.sigNum, big.firstNonZero)

    public fun toComplementedIntArray(
        big: BigInt): IntArray = toComplementedIntArray(big.mag, big.sigNum, big.firstNonZero)

    public fun getIdx(
        big: BigInt, index: Int): Int = getIdx(big.mag, big.sigNum, big.firstNonZero, index)
    public fun getIdxL(
        big: BigInt, index: Int): Long = getIdxL(big.mag, big.sigNum, big.firstNonZero, index)
    public fun getUnreversedIdx(
        big: BigInt, index: Int): Int = getUnreversedIdx(big.mag, big.sigNum, big.firstNonZero, index)

    public fun getIdx(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int, index: Int
    ): Int = when {
        index < 0 -> 0
        index >= mag.size -> sigNum.signed
        else -> getIdxInner<Unit>(sigNum, firstNonZero, index, revGet<Unit>(mag, index))
    }

    public fun getIdxL(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int, index: Int
    ): Long = getIdx(mag, sigNum, firstNonZero, index).toLong() and 0xffffffffL

    public fun getUnreversedIdx(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int, index: Int
    ): Int = getIdxInner<Unit>(sigNum, firstNonZero, index, mag[index])

    public fun intSize(bitLength: Int): Int = bitLength.floorDiv(TypeBits.int) + 1

    public fun toComplementedIntArray(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int
    ): IntArray = IntArray(mag.size) { getUnreversedIdx(mag, sigNum, firstNonZero, it) }

    public fun toInt(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int
    ): Int = getIdx(mag, sigNum, firstNonZero, 0)

    public fun toLong(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int
    ): Long = (getIdxL(mag, sigNum, firstNonZero, 1) shl TypeBits.int
            ) or getIdxL(mag, sigNum, firstNonZero, 0)

    public fun Int.getL(): Long = getL<Unit>(this)

    public fun IntArray.revIdx(index: Int): Int = revIdx<Unit>(this, index)
    public fun IntArray.revGet(index: Int): Int = revGet<Unit>(this, index)
    public fun IntArray.revSet(index: Int, value: Int) { revSet<Unit>(this, index, value) }

    public fun <E : MutableList<Int>> E.revSet(index: Int, value: Int): Unit = revSet<Unit, E>(this, index, value)

    public fun bigMask(pos: Int): Int = bigMask<Unit>(pos)

    public fun maxOfArrays(x: IntArray, y: IntArray, extra: Int = 1): IntArray =
        IntArray(max(x.size, y.size) + extra)

    public fun biggerFirst(
        x: BigInt, y: BigInt, block: (x: BigInt, y: BigInt) -> BigInt
    ): BigInt = when (x.mag.size < y.mag.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

    public fun setIdx(mag: IntArray, index: Int, num: Int): Unit = revSet<Unit>(mag, index, num)
    public fun setIdxL(mag: IntArray, index: Int, num: Long): Unit = revSet<Unit>(mag, index, num.toInt())
    public fun setUnreversedIdx(mag: IntArray, index: Int, num: Int) { setUnreversedIdx<Unit>(mag, index, num) }
    public fun setUnreversedIdxL(mag: IntArray, index: Int, num: Long): Unit = setUnreversedIdx<Unit>(mag, index, num.toInt())

    public fun bitLength(mag: IntArray, sigNum: BigSigned): Int = when {
        mag.isEmpty() -> 0
        sigNum.isNonNegative() -> (mag.lastIndex * TypeBits.int) + TypeBits.int - mag[0].countLeadingZeroBits()
        else -> {
            val pow2 = if(mag.size == 1) mag[0].countOneBits() == 1 else mag[0] == 0
            val magBitLength: Int = (mag.lastIndex * TypeBits.int) + TypeBits.int - mag[0].countLeadingZeroBits()
            if (pow2) magBitLength - 1 else magBitLength
        }
    }

    public fun bitCount(mag: IntArray, sigNum: BigSigned): Int {
        var count = mag.sumOf { it.countOneBits() }
        if (sigNum.isNegative()) {
            var magTrailingZeroCount = 0
            var j: Int = mag.lastIndex
            while (mag[j] == 0) {
                magTrailingZeroCount += TypeBits.int
                j--
            }
            magTrailingZeroCount += mag[j].countTrailingZeroBits()
            count += magTrailingZeroCount - 1
        }
        return count
    }

    public fun firstNonZero(mag: IntArray): Int = (
            mag.lastIndex downTo 0).indexOfFirst { mag[it] != 0 }.let { if (it == -1) 0 else it }

    public fun bitSizeForInt(n: Int): Int = TypeBits.int - n.countLeadingZeroBits()

    public fun fromBinary(value: Binary): BigInt {
        require(value.limit > 0) { BigMathException("ByteArray must not be of zero length.") }
        val negative = value.retrieveByte(0).toInt() < 0

        val sigNum = when (negative) {
            true -> BigSigned.NEGATIVE
            else -> BigSigned.POSITIVE
        }
        val mag = when (negative) {
            true -> makePositive<Unit>(value)
            else -> stripLeadingZeros(value)
        }

        return BigInt(mag, sigNumZeroAdjust<Unit>(mag, sigNum))
    }

    public fun fromByteArray(value: ByteArray): BigInt {
        require(value.isNotEmpty()) { BigMathException("ByteArray must not be of zero length.") }
        val negative = value.first().toInt() < 0

        val sigNum = when (negative) {
            true -> BigSigned.NEGATIVE
            else -> BigSigned.POSITIVE
        }
        val mag = when (negative) {
            true -> makePositive<Unit>(value)
            else -> stripLeadingZeros(value)
        }

        return BigInt(mag, sigNumZeroAdjust<Unit>(mag, sigNum))
    }

    public fun stripLeadingZeros(value: Binary): IntArray {
        val keep = keep<Unit>(value, BigSigned.POSITIVE)
        val result = IntArray((value.limit - keep + 3).floorDiv(TypeSize.int))
        val cache = ByteArray(result.size * TypeSize.int)
        val diff  = cache.size - value.limit
        (keep..< value.limit).forEach { cache[it + diff] = value.retrieveByte(it) }

        stripLeadingZerosNormalize<Unit>(result, cache)
        return result
    }

    public fun stripLeadingZeros(value: ByteArray): IntArray {
        val keep = keep<Unit>(value, BigSigned.POSITIVE)
        val result = IntArray((value.size - keep + 3).floorDiv(TypeSize.int))
        val cache = ByteArray(
            result.size * TypeSize.int - (
                    value.size - keep)
        ) + value.copyOfRange(keep, value.size)

        stripLeadingZerosNormalize<Unit>(result, cache)
        return result
    }

    public fun sigNumZeroAdjust(
        mag: IntArray,
        sigNum: BigSigned
    ): BigSigned = sigNumZeroAdjust<Unit>(mag, sigNum)

    public fun fromIntArray(value: IntArray): BigInt {
        require(value.isNotEmpty()) { BigMathException("IntArray must not be of zero length.") }
        val negative = value.first() < 0

        val sigNum = when (negative) {
            true -> BigSigned.NEGATIVE
            else -> BigSigned.POSITIVE
        }
        val mag = when (negative) {
            true -> makePositive<Unit>(value)
            else -> stripLeadingZeros<Unit>(value)
        }

        return BigInt(mag, sigNumZeroAdjust<Unit>(mag, sigNum))
    }

    public fun fromLong(value: Long): BigInt {
        val negative = value < 0

        val sigNum = when (negative) {
            true -> BigSigned.NEGATIVE
            else -> BigSigned.POSITIVE
        }
        val tmp = intArrayOf((value ushr TypeBits.int).toInt(), value.toInt())
        val mag = when (negative) {
            true -> makePositive<Unit>(tmp)
            else -> stripLeadingZeros<Unit>(tmp)
        }

        return BigInt(mag, sigNumZeroAdjust<Unit>(mag, sigNum))
    }

    public fun getByteSize(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int
    ): Int {
        if (sigNum.isZero()) return 1

        val output = ByteArray(mag.size * 4)
        mag.indices.forEach {
            output.writeIntAt(
                it * 4,
                getIdx(mag, sigNum, firstNonZero, mag.lastIndex - it).swapEndian()
            ) }
        val keep = keep<Unit>(output, sigNum)

        if (keep == output.size) return 1

        val prepend = sigNum.isNonNegative() == output[keep] < 0
        return when {
            keep == 0 && prepend -> 1 + output.size
            keep == 1 && prepend -> output.size
            keep > 1 && prepend -> 1 + output.size - keep
            keep > 0 -> output.size - keep
            else -> output.size
        }
    }

    public fun toByteArray(
        mag: IntArray, sigNum: BigSigned, firstNonZero: Int
    ): ByteArray {
        if (sigNum.isZero()) return byteArrayOf(0)

        val output = ByteArray(mag.size * 4)
        mag.indices.forEach {
            output.writeIntAt(
                it * 4,
                getIdx(mag, sigNum, firstNonZero, mag.lastIndex - it).swapEndian()
            ) }
        val keep = keep<Unit>(output, sigNum)

        if (keep == output.size) return byteArrayOf(sigNum.signed.toByte())

        val prepend = sigNum.isNonNegative() == output[keep] < 0
        return when {
            keep == 0 && prepend -> byteArrayOf(sigNum.signed.toByte()) + output
            keep == 1 && prepend -> output.also { it[0] = sigNum.signed.toByte() }
            keep > 1 && prepend -> byteArrayOf(sigNum.signed.toByte()) + output.copyOfRange(keep, output.size)
            keep > 0 -> output.copyOfRange(keep, output.size)
            else -> output
        }
    }

    public fun emptyBigIntOf(value: IntArray = intArrayOf(0)): BigInt = BigInt(value.copyOf(), BigSigned.POSITIVE)

    public companion object : AbstractMathLogic()
}

public object MathLogicContext : MathLogic
public fun <T> withLogic(block: MathLogicContext.() -> T): T = MathLogicContext.block()
