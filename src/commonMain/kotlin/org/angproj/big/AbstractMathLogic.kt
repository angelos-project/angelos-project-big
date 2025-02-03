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
 *      Josh Bloch - partial early implementation
 *      Michael McCloskey - partial early implementation
 *      Alan Eliasen - partial early implementation
 *      Timothy Buktu - partial early implementation
 *      Kristoffer Paulsson - partial initial implementation
 */
package org.angproj.big

import org.angproj.aux.buf.IntBuffer
import org.angproj.aux.buf.copyInto
import org.angproj.aux.io.Binary
import org.angproj.aux.io.TypeBits
import org.angproj.aux.io.TypeSize
import org.angproj.aux.util.BufferAware
import org.angproj.aux.util.swapEndian


public abstract class AbstractMathLogic: BufferAware {

    protected inline fun <reified R: Any> name() {}

    protected inline fun <reified R: Any>getIdxInner(
        sigNum: BigSigned, firstNonZero: Int, index: Int, num: Int
    ): Int = when {
        isNonNegative<Unit>(sigNum) -> num
        index <= firstNonZero -> -num
        else -> num.inv()
    }

    protected inline fun <reified R: Any, E: List<Int>> getIdx(
        mag: E, sigNum: BigSigned, firstNonZero: Int, index: Int
    ): Int = when {
        index < 0 -> 0
        index >= mag.size -> sigNum.signed
        else -> getIdxInner<Unit>(sigNum, firstNonZero, index, revGet<Unit, E>(mag, index))
    }

    protected inline fun <reified R: Any, E: List<Int>> getUnreversedIdx(
        mag: E, sigNum: BigSigned, firstNonZero: Int, index: Int
    ): Int = getIdxInner<Unit>(sigNum, firstNonZero, index, mag[index])

    protected inline fun <reified R: Any, E: List<Int>> getIdxL(
        mag: E, sigNum: BigSigned, firstNonZero: Int, index: Int
    ): Long = getIdx<Unit, E>(mag, sigNum, firstNonZero, index).toLong() and 0xffffffffL

    protected inline fun <reified R: Any> getL(value: Int): Long = value.toLong() and 0xffffffffL

    protected inline fun <reified R: Any> revIdx(mag: IntArray, index: Int): Int = mag.lastIndex - index
    protected inline fun <reified R: Any> revGet(mag: IntArray, index: Int): Int = mag[mag.lastIndex - index]
    protected inline fun <reified R: Any> revSet(mag: IntArray,index: Int, value: Int) { mag[mag.lastIndex - index] = value }

    protected inline fun <reified R: Any, E : MutableList<Int>>revSet(mag: E, index: Int, value: Int) { mag[mag.lastIndex - index] = value }
    protected inline fun <reified R: Any, E : List<Int>> getL(mag: E, index: Int): Long = getL<R>(mag[index])
    protected inline fun <reified R: Any, E : List<Int>> revGet(mag: E, index: Int): Int = mag[mag.lastIndex - index]
    protected inline fun <reified R: Any, E : List<Int>> revGetL(mag: E, index: Int): Long = getL<R>(mag[mag.lastIndex - index])

    protected inline fun <reified R: Any, E: MutableList<Int>> setUnreversedIdx(mag: E, index: Int, num: Int) { mag[index] = num }

    protected inline fun <reified R: Any> setUnreversedIdx(mag: IntArray, index: Int, num: Int) { mag[index] = num }

    protected inline fun <reified R: Any> bigMask(pos: Int): Int = 1 shl (pos and TypeBits.int - 1)

    protected inline fun <reified R: Any> isNonNegative(sigNum: BigSigned): Boolean = when (sigNum) {
        BigSigned.NEGATIVE -> false
        else -> true
    }

    protected inline fun <reified R: Any> sigNumZeroAdjust(
        mag: IntArray,
        sigNum: BigSigned
    ): BigSigned = when {
        mag.isEmpty() -> BigSigned.ZERO
        else -> sigNum
    }

    protected inline fun <reified R: Any> keep(value: ByteArray, sigNum: BigSigned): Int {
        val keep = value.indexOfFirst { it.toInt() != sigNum.signed }
        return when (keep) {
            -1 -> value.size
            else -> keep
        }
    }

    protected inline fun <reified R: Any> keep(value: Binary, sigNum: BigSigned): Int {
        val keep = value.indices.indexOfFirst{ value.retrieveByte(it).toInt() != sigNum.signed }
        return when (keep) {
            -1 -> value.limit
            else -> keep
        }
    }

    protected inline fun <reified R: Any> keep(value: IntArray, sigNum: BigSigned): Int {
        val keep = value.indexOfFirst { it != sigNum.signed }
        return when (keep) {
            -1 -> value.size
            else -> keep
        }
    }

    protected inline fun <reified R: Any> stripLeadingZerosNormalize(value: IntArray, cache: ByteArray) {
        (value.lastIndex downTo 0).forEach {
            value[it] = cache.readRevIntAt(it * TypeSize.int)
        }
    }

    protected inline fun <reified R: Any> stripLeadingZeros(value: IntArray): IntArray {
        val keep = keep<Unit>(value, BigSigned.POSITIVE)
        return if (keep == 0) value else value.copyOfRange(keep, value.size)
    }

    protected inline fun <reified R: Any> makePositiveNormalize(value: IntArray) {
        (value.lastIndex downTo 0).indexOfFirst {
            value[it] = ((value[it].toLong() and 0xffffffffL) + 1).toInt()
            value[it] != 0
        }
    }

    protected inline fun <reified R: Any> makePositiveCopy(value: IntArray, cache: ByteArray) {
        (value.lastIndex downTo 0).forEach {
            val num = cache.readIntAt(it * TypeSize.int)
            value[it] = when {
                num < 0 -> num.inv().swapEndian()
                else -> num.swapEndian().inv()
            }
        }
    }

    protected inline fun <reified R: Any> makePositive(value: IntArray): IntArray {
        val keep: Int = keep<Unit>(value, BigSigned.NEGATIVE)
        val extra = (keep until value.size).indexOfFirst { value[it] != 0 }.let { if (it == -1) 1 else 0 }
        val result = IntArray(value.size - keep + extra)

        (keep until value.size).forEach { result[it - keep + extra] = value[it].inv() }

        makePositiveNormalize<Unit>(result)
        return result
    }

    protected inline fun <reified R: Any> makePositive(value: Binary): IntArray {
        val keep = keep<Unit>(value, BigSigned.NEGATIVE)
        val extra = (keep until value.limit).indexOfFirst { value.retrieveByte(it).toInt() != 0 }.let { if (it == -1) 1 else 0 }
        val result = IntArray((value.limit - keep + extra + 3).floorDiv(TypeSize.int))
        val cache = ByteArray(result.size * TypeSize.int).also {
            it.fill(BigSigned.NEGATIVE.signed.toByte()) }
        val diff = cache.size - value.limit
        (keep..< value.limit).forEach { cache[it + diff] = value.retrieveByte(it) }

        makePositiveCopy<Unit>(result, cache)
        makePositiveNormalize<Unit>(result)
        return result
    }

    protected inline fun <reified R: Any> makePositive(value: ByteArray): IntArray {
        val keep = keep<Unit>(value, BigSigned.NEGATIVE)
        val extra = (keep until value.size).indexOfFirst { value[it].toInt() != 0 }.let { if (it == -1) 1 else 0 }
        val result = IntArray((value.size - keep + extra + 3).floorDiv(TypeSize.int))
        val cache = ByteArray(result.size * TypeSize.int - (value.size - keep)).also {
            it.fill(BigSigned.NEGATIVE.signed.toByte())
        } + value.copyOfRange(keep, value.size)

        makePositiveCopy<Unit>(result, cache)
        makePositiveNormalize<Unit>(result)
        return result
    }
}


public fun IntBuffer.isEmpty(): Boolean = limit == 0
public fun IntBuffer.isNotEmpty(): Boolean = limit > 0


public fun IntBuffer.copyOfRange(keep: Int, limit: Int): IntBuffer {
    return IntBuffer(limit - keep).also { this.copyInto(it, 0, keep, limit) }
}

public fun Binary.iterator(): Iterator<Byte> = object: Iterator<Byte> {
    private var index = 0
    override fun hasNext(): Boolean = index < limit
    override fun next(): Byte = retrieveByte(index++)
}

public val Binary.indices: IntRange
    get() = 0..< limit
public val Binary.lastIndex: Int
    get() = limit - 1