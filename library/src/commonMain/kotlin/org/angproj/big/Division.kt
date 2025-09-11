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
 *      Michael McCloskey
 *      Timothy Buktu
 *
 * Contributors:
 *      Kristoffer Paulsson - Port to Kotlin and adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.sec.util.ensure


public operator fun BigInt.div(other: BigInt): BigInt = divide(other)

public operator fun BigInt.rem(other: BigInt): BigInt = remainder(other)

public fun BigInt.divide(value: BigInt): BigInt = divideAndRemainder(value).first

public fun BigInt.remainder(value: BigInt): BigInt = divideAndRemainder(value).second

public fun BigInt.divideAndRemainder(
    value: BigInt
): Pair<BigInt, BigInt> = when {
    value.sigNum.isZero() -> ensure { BigMathException("Divisor can not be zero.") }
    value.compareSpecial(BigInt.one) == BigCompare.EQUAL -> Pair(this, BigInt.zero)
    sigNum.isZero() -> Pair(BigInt.zero, BigInt.zero)
    else -> {
        val cmp = BigInt.innerCompareMagnitude(this.mag, value.mag)
        when {
            cmp.isLesser() -> Pair(BigInt.zero, this)
            cmp.isEqual() -> when {
                value.sigNum != this.sigNum -> Pair(BigInt.minusOne, BigInt.zero)
                else -> Pair(BigInt.one, BigInt.zero)
            }
            else -> {
                val qabs = this.abs()
                val vabs = value.abs()
                val result = when {
                    value.mag.size == 1 -> divideOneWord(
                        qabs.mag, qabs.sigNum,
                        vabs.mag, vabs.sigNum
                    )
                    else -> divideMagnitude(qabs.mag, vabs.mag)
                }
                Pair(
                    ExportImportBigInt.externalOf(
                        if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE, result.first
                    ),
                    ExportImportBigInt.externalOf(this.sigNum, result.second)
                )
            }
        }
    }
}

/**
 * Adaption from Java BigInteger.
 * */

internal fun divideOneWord(
    dividend: IntArray, dividendSig: BigSigned,
    divisor: IntArray, divisorSig: BigSigned
): Pair<IntArray, IntArray> {
    val dividendNz = dividend.firstNonzero()
    val divisorNz = divisor.firstNonzero()

    //val sorLong = divisor.getIdxL(divisor.lastIndex)
    val sorLong = divisor.intGetComp(
        divisor.lastIndex, divisorSig, divisorNz).longMask()
    val sorInt = sorLong.toInt()

    if (dividend.size == 1) {
        //val dendValue: Long = dividend.getIdxL(dividend.lastIndex)
        val dendValue: Long = dividend.intGetComp(
            dividend.lastIndex, dividendSig, dividendNz).longMask()
        val q = (dendValue / sorLong) // Int ?
        val r = (dendValue - q * sorLong) // Int ?
        //val q: Int = (dividendValue / divisorLong).toInt()
        //val r: Int = (dividendValue - q * divisorLong).toInt()
        return Pair(
            intArrayOf(q.toInt()),
            intArrayOf(r.toInt()),
        )
    }
    val quotient = IntArray(dividend.size)

    val shift: Int = sorInt.countLeadingZeroBits()
    //var rem: Int = dividend.getUnreversedIdx(0)
    var rem: Int = dividend.intGetCompUnrev(
        0, dividendSig, dividendNz)
    var remLong = rem.longMask()
    if (remLong < sorLong) {
        // quotient.setUnreversedIdx(0, 0)
        quotient[0] = 0
    } else {
        //quotient.setUnreversedIdxL(0, remLong / sorLong)
        quotient[0] = (remLong / sorLong).toInt()
        //rem = (remLong - quotient.getUnreversedIdx(0) * sorLong).toInt()
        rem = (remLong - quotient[0] * sorLong).toInt()
        remLong = rem.longMask()
    }

    (dividend.lastIndex downTo 1).forEach { idx ->
        //val dendEst = remLong shl Int.SIZE_BITS or dividend.getIdxL(idx - 1)
        val dendEst = remLong shl Int.SIZE_BITS or dividend.intGetComp(
            idx - 1, dividendSig, dividendNz).longMask()
        var q: Int
        if (dendEst >= 0) {
            q = (dendEst / sorLong).toInt()
            rem = (dendEst - q * sorLong).toInt()
        } else {
            val tmp = divWord(dendEst, sorInt)
            q = (tmp and 0xffffffffL).toInt()
            rem = (tmp ushr Int.SIZE_BITS).toInt()
        }
        //quotient.setIdx(idx - 1, q)
        quotient.intSet(idx - 1, q)
        remLong = rem.longMask()
    }

    return Pair(
        quotient,
        intArrayOf(if(shift > 0) rem % sorInt else rem)
    )
}

internal fun divideMagnitude(dividend: IntArray, divisor: IntArray): Pair<IntArray, IntArray> {
    val shift = divisor[0].countLeadingZeroBits()

    val sorLen = divisor.size
    val sorArr: IntArray = when {
        shift > 0 -> IntArray(divisor.size).also {
            copyAndShift(divisor, 0, divisor.size, it, 0, shift) }
        else -> divisor.copyOfRange(0, divisor.size)
    }
    val remArr: IntArray = when {
        shift <= 0 -> IntArray(dividend.size + 1).also { arr ->
            dividend.copyInto(arr, 1, 0, dividend.size)
        }
        dividend[0].countLeadingZeroBits() >= shift -> IntArray(dividend.size + 1).also { arr ->
            copyAndShift(dividend, 0, arr.lastIndex, arr, 1, shift)
        }
        else -> IntArray(dividend.size + 2).also { arr ->
            var c = 0
            val n2 = Int.SIZE_BITS - shift
            (1 until dividend.size + 1).forEach { idx ->
                val b = c
                c = dividend[idx - 1]
                arr[idx] = b shl shift or (c ushr n2)
            }
            arr[dividend.size + 1] = c shl shift
        }
    }

    val remLen = remArr.lastIndex
    val quotLen = remLen - sorLen + 1
    val quotArr = IntArray(quotLen)

    val sorHigh = sorArr[0]
    val sorHighLong = sorHigh.longMask()
    val sorLow = sorArr[1]

    quotArr.indices.forEach { idx ->
        var qhat: Int
        var qrem: Int
        var skipCorrection = false
        val nh = remArr[idx]
        val nh2 = nh + -0x80000000
        val nm = remArr[idx + 1]
        if (nh == sorHigh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = nh.toLong() shl Int.SIZE_BITS or nm.longMask()
            if (nChunk >= 0) {
                qhat = (nChunk / sorHighLong).toInt()
                qrem = (nChunk - qhat * sorHighLong).toInt()
            } else {
                val tmp = divWord(nChunk, sorHigh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr Int.SIZE_BITS).toInt()
            }
        }
        if (qhat == 0) return@forEach
        if (!skipCorrection) {
            val nl = remArr[idx + 2].longMask()
            var rs = qrem.longMask() shl Int.SIZE_BITS or nl
            var estProd = sorLow.longMask() * qhat.longMask()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.longMask() + sorHighLong).toInt()
                if (qrem.longMask() >= sorHighLong) {
                    estProd -= sorLow.longMask()
                    rs = qrem.longMask() shl Int.SIZE_BITS or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[idx] = 0
        val borrow = mulSub(remArr, sorArr, qhat, sorLen, idx)

        if (borrow + -0x80000000 > nh2) {
            divAdd(sorArr, remArr, idx + 1)
            qhat--
        }

        quotArr[idx] = qhat
    }

    var qhat: Int
    var qrem: Int
    var skipCorrection = false
    val nh = remArr[quotLen - 1]
    val nh2 = nh + -0x80000000
    val nm = remArr[quotLen]
    if (nh == sorHigh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = nh.toLong() shl Int.SIZE_BITS or nm.longMask()
        if (nChunk >= 0) {
            qhat = (nChunk / sorHighLong).toInt()
            qrem = (nChunk - qhat * sorHighLong).toInt()
        } else {
            val tmp = divWord(nChunk, sorHigh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr Int.SIZE_BITS).toInt()
        }
    }
    /*if (qhat != 0) { // appendectomy
        if (!skipCorrection) {
            val nl = remArr[quotLen + 1].longMask()
            var rs = qrem.longMask() shl Int.SIZE_BITS or nl
            var estProd = sorLow.longMask() * qhat.longMask()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.longMask() + sorHighLong).toInt()
                if (qrem.longMask() >= sorHighLong) {
                    estProd -= sorLow.longMask()
                    rs = qrem.longMask() shl Int.SIZE_BITS or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[quotLen - 1] = 0
        val borrow = mulSub(remArr, sorArr, qhat, sorLen, quotLen - 1)

        if (borrow + -0x80000000 > nh2) {
            divAdd(sorArr, remArr, quotLen)
            qhat--
        }

        quotArr[quotLen - 1] = qhat
    }*/

    return Pair(
        quotArr,
        if (shift > 0) rightShift(remArr, shift) else remArr
    )
}


internal fun divWord(n: Long, d: Int): Long {
    val dLong = d.longMask()
    var r: Long
    var q: Long
    if (dLong == 1L) {
        q = n.toInt().toLong()
        r = 0
        return r shl Int.SIZE_BITS or (q and 0xffffffffL)
    }

    q = (n ushr 1) / (dLong ushr 1)
    r = n - q * dLong

    while (r < 0) {
        r += dLong
        q--
    }
    while (r >= dLong) {
        r -= dLong
        q++
    }
    return r shl Int.SIZE_BITS or (q and 0xffffffffL)
}

internal fun rightShift(value: IntArray, n: Int): IntArray {
    if (value.size == 0) return value
    val nInts = n ushr 5
    val nBits = n and 0x1F
    val value2 = value.copyOf(value.size - nInts)
    if (nBits == 0) return value2
    val bitsInHighWord = Int.SIZE_BITS - value2[0].countLeadingZeroBits()
    return if (nBits >= bitsInHighWord) {
        primitiveLeftShift(value2, Int.SIZE_BITS - nBits).copyOf(value.lastIndex)
    } else {
        primitiveRightShift(value2, nBits)
    }
}

internal fun primitiveRightShift(value: IntArray, n: Int): IntArray {
    val n2 = Int.SIZE_BITS - n
    var c = value[value.lastIndex]
    (value.lastIndex downTo 1).forEach { idx ->
        val b = c
        c = value[idx - 1]
        value[idx] = c shl n2 or (b ushr n)
    }
    value[0] = value[0] ushr n
    return value
}

internal fun primitiveLeftShift(value: IntArray, n: Int): IntArray {
    val n2 = Int.SIZE_BITS - n
    var c = value[0]
    (0 until value.lastIndex).forEach { idx ->
        val b = c
        c = value[idx + 1]
        value[idx] = b shl n or (c ushr n2)
    }
    value[value.lastIndex] = value[value.lastIndex] shl n
    return value
}

internal fun copyAndShift(
    src: IntArray, srcFrom_: Int, srcLen: Int,
    dst: IntArray, dstFrom: Int, shift: Int
) {
    var srcFrom = srcFrom_
    val n2 = Int.SIZE_BITS - shift
    var c = src[srcFrom]
    for (i in 0 until srcLen - 1) {
        val b = c
        c = src[++srcFrom]
        dst[dstFrom + i] = b shl shift or (c ushr n2)
    }
    dst[dstFrom + srcLen - 1] = c shl shift
}

internal fun mulSub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    var carry: Long = 0
    (len - 1 downTo 0).forEach { idx ->
        val prod: Long = a[idx].longMask() * x.longMask() + carry
        val diff = q[offset + idx + 1] - prod
        q[offset + idx + 1] = diff.toInt()
        carry = (prod ushr Int.SIZE_BITS) + (
                if ((diff and 0xffffffffL) > (prod.inv() and 0xffffffffL)) 1 else 0)
    }
    return carry.toInt()
}

internal fun divAdd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0
    (a.lastIndex downTo 0).forEach { idx ->
        val sum: Long = a[idx].longMask() + result[idx + offset].longMask() + carry
        result[idx + offset] = sum.toInt()
        carry = sum ushr Int.SIZE_BITS
    }
    return carry.toInt()
}