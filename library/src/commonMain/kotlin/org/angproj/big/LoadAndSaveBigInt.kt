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
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ensure


public fun Int.longMask(): Long = this.toLong() and 0xFFFFFFFFL

public fun IntArray.longSet(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }

public fun IntArray.intSet(index: Int, value: Int) { this[lastIndex - index] = value }

public fun IntArray.intGet(index: Int): Int = this[lastIndex - index]

public fun IntArray.firstNonzero(): Int = LoadAndSaveBigInt.firstNonZeroIntNum(this)

public fun IntArray.intLength(sigNum: BigSigned): Int = LoadAndSaveBigInt.intLength(this, sigNum)

public fun IntArray.intGetComp(
    index: Int, sigNum: BigSigned, firstNonZero: Int
): Int = LoadAndSaveBigInt.getInt(index, this,  sigNum, firstNonZero)

public fun IntArray.intGetCompUnrev(
    index: Int, sigNum: BigSigned, firstNonZero: Int
): Int = LoadAndSaveBigInt.getIntUnrev(index, this,  sigNum, firstNonZero)

public fun IntArray.rev(index: Int): Int = this.lastIndex - index


public object LoadAndSaveBigInt {

    public fun bitLengthForInt(n: Int): Int = TypeSize.intBits - n.countLeadingZeroBits()

    public fun intLength(mag: IntArray, sigNum: BigSigned): Int = (bitLength(mag, sigNum) ushr 5) + 1

    public fun bitLength(mag: IntArray, sigNum: BigSigned): Int = when (mag.isEmpty()) {
        true -> 0
        else -> {
            val magBitLength: Int = (mag.lastIndex shl 5) + bitLengthForInt(mag[0])
            when (sigNum.isNegative()) {
                true -> {
                    // Check if magnitude is a power of two
                    var pow2 = mag[0].countOneBits() == 1
                    var i = 1
                    while (i < mag.size && pow2) {
                        pow2 = mag[i] == 0
                        i++
                    }

                    when(pow2) {
                        true -> magBitLength -1
                        else -> magBitLength
                    }
                }
                else -> magBitLength
            }
        }
    }

    public fun bitCount(mag: IntArray, sigNum: BigSigned): Int {
        var count = mag.sumOf { it.countOneBits() }
        if (sigNum.isNegative()) {
            var magTrailingZeroCount = 0
            var j: Int = mag.lastIndex
            while (mag[j] == 0) {
                magTrailingZeroCount += TypeSize.intBits
                j--
            }
            magTrailingZeroCount += mag[j].countTrailingZeroBits()
            count += magTrailingZeroCount - 1
        }
        return count
    }

    private fun signInt(sigNum: BigSigned): Int = if (sigNum.isNegative()) -1 else 0

    public fun firstNonZeroIntNum(mag: IntArray): Int {
        val mlen: Int = mag.size
        var i: Int = mlen - 1
        while (i >= 0 && mag[i] == 0) { i-- }
        return mlen - i - 1
    }

    public fun getInt(n: Int, mag: IntArray, sigNum: BigSigned, firstNonZero: Int): Int {
        if (n < 0) return 0
        if (n >= mag.size) return signInt(sigNum)

        val magInt: Int = mag[mag.lastIndex - n]

        return if (sigNum.isNonNegative()) magInt else if (n <= firstNonZero) -magInt else magInt.inv()
    }

    public fun getIntUnrev(n: Int, mag: IntArray, sigNum: BigSigned, firstNonZero: Int): Int {
        if (n < 0) return 0
        if (n >= mag.size) return signInt(sigNum)

        val magInt: Int = mag[n]

        return if (sigNum.isNonNegative()) magInt else if (n <= firstNonZero) -magInt else magInt.inv()
    }

    public fun toByteArray(mag: IntArray, sigNum: BigSigned): ByteArray = toArbitraryByte(
        mag, sigNum,
        { idx, data -> this[idx] = data }
    ) { ByteArray(it) }

    public fun <E> toArbitraryByte(
        mag: IntArray, sigNum: BigSigned,
        writeOctet: E.(Int, Byte) -> Unit,
        factory: (Int) -> E
    ): E {
        val firstNonZero = firstNonZeroIntNum(mag)
        val byteLen: Int = bitLength(mag, sigNum) / 8 + 1
        val byteData = factory(byteLen)

        var i = byteLen - 1
        var bytesCopied = 4
        var nextInt = 0
        var intIndex = 0
        while (i >= 0) {
            if (bytesCopied == 4) {
                nextInt = getInt(intIndex++, mag, sigNum, firstNonZero)
                bytesCopied = 1
            } else {
                nextInt = nextInt ushr 8
                bytesCopied++
            }
            byteData.writeOctet(i, nextInt.toByte())
            i--
        }
        return byteData
    }

    // Read
    public fun internalOf(bytes: ByteArray): BigInt = internalOf(bytes, bytes.size) { this[it] }

    private fun <E> stripLeadingZeroBytes(
        firstOctet: Int, data: E, size: Int, readOctet: E.(i: Int) -> Byte
    ): IntArray {
        var octet = firstOctet
        var index = 1

        while (octet == BigSigned.POSITIVE.signed && index < size) {
            octet = data.readOctet(index++).toInt() }
        if (octet == 0) { return intArrayOf() }

        val result = IntArray((size - index).div(4) + 1)

        var first = octet and 0xFF
        repeat((size - index).mod(4)) {
            first = first shl 8 or (data.readOctet(index++).toInt() and 0xff)
        }
        result[0] = first

        repeat ((size - index).div(4)) {
            result[it + 1] = (data.readOctet(index++).toInt() shl 24) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 16) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 8) or
                    (data.readOctet(index++).toInt() and 0xff)
        }
        return result
    }

    private fun <E> makePositive(
        firstOctet: Int, data: E, size: Int, readOctet: E.(i: Int) -> Byte
    ): IntArray {
        var octet: Int = firstOctet
        var index = 1

        while (octet == BigSigned.NEGATIVE.signed && index < size) {
            octet = data.readOctet(index++).toInt() }

        var first = -1 shl 8 or (octet and 0xFF)
        repeat((size - index).mod(4)) {
            octet = data.readOctet(index++).toInt()
            first = first shl 8 or (octet and 0xFF)
        }
        val realSize = index

        while (octet == BigSigned.POSITIVE.signed && index < size) {
            octet = data.readOctet(index++).toInt() }

        var second = octet and 0xFF
        repeat((size - index).mod(4)) {
            second = second shl 8 or (data.readOctet(index++).toInt() and 0xff)
        }

        val extra = if ((size - index or first or second) == 0) 1 else 0
        val result = IntArray(extra + 1 + (size - realSize).div(4))
        result[0] = if (extra == 0) first else -1
        var idx = result.size - (size - index).div(4)
        if (idx > 1) result[idx - 1] = second

        repeat ((size - index).div(4)) {
            result[idx++] = (data.readOctet(index++).toInt() shl 24) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 16) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 8) or
                    (data.readOctet(index++).toInt() and 0xff)
        }

        while (--idx >= 0 && result[idx] == 0) { Unit }
        result[idx] = -result[idx]
        while (--idx >= 0) { result[idx] = result[idx].inv() }
        return result
    }

    public fun <E> internalOf(data: E, size: Int, readOctet: E.(i: Int) -> Byte) : BigInt {
        ensure(size > 0) { BigMathException("Zero length magnitude") }

        val firstOctet = data.readOctet(0).toInt()
        return when(firstOctet < 0) {
            true -> BigInt(makePositive(firstOctet, data, size, readOctet), BigSigned.NEGATIVE)
            else -> {
                val mag = stripLeadingZeroBytes(firstOctet, data, size, readOctet)
                BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
            }
        }
    }
}