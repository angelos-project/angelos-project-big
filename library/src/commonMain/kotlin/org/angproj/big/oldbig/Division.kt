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


public operator fun BigInt.div(other: BigInt): BigInt = divide(other)

public operator fun BigInt.rem(other: BigInt): BigInt = remainder(other)

public fun BigInt.divide(value: BigInt): BigInt = divideAndRemainder(value).first

public fun BigInt.remainder(value: BigInt): BigInt = divideAndRemainder(value).second

public fun BigInt.divideAndRemainder(
    value: BigInt
): Pair<BigInt, BigInt> = when {
    value.sigNum.isZero() -> throw BigMathException("Divisor can not be zero.")
    value.compareSpecial(BigInt.one) == BigCompare.EQUAL -> Pair(this, BigInt.zero)
    sigNum.isZero() -> Pair(BigInt.zero, BigInt.zero)
    else -> {
        val cmp = MutableBigInt.compareMagnitude(this, value)
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
                    value.mag.size == 1 -> MutableBigInt.divideOneWord(qabs, vabs)
                    else -> MutableBigInt.divideMagnitude(qabs, vabs)
                }
                val q = BigMath.stripLeadingZeros(result.first.toComplementedIntArray())
                val r = BigMath.stripLeadingZeros(result.second.toComplementedIntArray())
                Pair(
                    BigInt(
                        q.toList(),
                        if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE
                    ),
                    BigInt(
                        r.toList(),
                        BigMath.sigNumZeroAdjust(r, this.sigNum)
                    )
                )
            }
        }
    }
}

/**
 * Adaption from Java BigInteger.
 * */

internal fun MutableBigInt.Companion.divideOneWord(
    dividend: BigMath<*>,
    divisor: BigMath<*>,
): Pair<MutableBigInt, MutableBigInt> {
    val sorLong = divisor.getIdxL(divisor.mag.lastIndex)
    val sorInt = sorLong.toInt()

    if (dividend.mag.size == 1) {
        val dendValue: Long = dividend.getIdxL(dividend.mag.lastIndex)
        val q = (dendValue / sorLong)
        val r = (dendValue - q * sorLong)
        return Pair(
            ofLong(q),
            ofLong(r),
        )
    }
    val quotient = emptyMutableBigIntOf(IntArray(dividend.mag.size))

    val shift: Int = sorInt.countLeadingZeroBits()
    var rem: Int = dividend.getUnreversedIdx(0)
    var remLong = rem.getL()
    if (remLong < sorLong) {
        quotient.setUnreversedIdx(0, 0)
    } else {
        quotient.setUnreversedIdxL(0, remLong / sorLong)
        rem = (remLong - quotient.getUnreversedIdx(0) * sorLong).toInt()
        remLong = rem.getL()
    }

    (dividend.mag.lastIndex downTo 1).forEach { idx ->
        val dendEst = remLong shl Int.SIZE_BITS or dividend.getIdxL(idx - 1)
        var q: Int
        if (dendEst >= 0) {
            q = (dendEst / sorLong).toInt()
            rem = (dendEst - q * sorLong).toInt()
        } else {
            val tmp = MutableBigInt.divWord(dendEst, sorInt)
            q = (tmp and 0xffffffffL).toInt()
            rem = (tmp ushr Int.SIZE_BITS).toInt()
        }
        quotient.setIdx(idx - 1, q)
        remLong = rem.getL()
    }

    return Pair(
        quotient,
        ofLong((if(shift > 0) rem % sorInt else rem).toLong())
    )
}

internal fun MutableBigInt.Companion.divideMagnitude(
    dividend: BigMath<*>,
    divisor: BigMath<*>,
): Pair<MutableBigInt, MutableBigInt> {
    val shift = divisor.mag[0].countLeadingZeroBits()

    val sorLen = divisor.mag.size
    val sorArr: IntArray = when {
        shift > 0 -> IntArray(divisor.mag.size).also {
            MutableBigInt.copyAndShift(divisor.mag.toIntArray(), 0, divisor.mag.size, it, 0, shift) }
        else -> divisor.mag.toIntArray().copyOfRange(0, divisor.mag.size)
    }
    val remArr: IntArray = when {
        shift <= 0 -> IntArray(dividend.mag.size + 1).also { arr ->
            dividend.mag.toIntArray().copyInto(arr, 1, 0, dividend.mag.size)
        }
        dividend.mag[0].countLeadingZeroBits() >= shift -> IntArray(dividend.mag.size + 1).also { arr ->
            MutableBigInt.copyAndShift(dividend.mag.toIntArray(), 0, arr.lastIndex, arr, 1, shift)
        }
        else -> IntArray(dividend.mag.size + 2).also { arr ->
            var c = 0
            val n2 = Int.SIZE_BITS - shift
            (1 until dividend.mag.size + 1).forEach { idx ->
                val b = c
                c = dividend.mag[idx - 1]
                arr[idx] = b shl shift or (c ushr n2)
            }
            arr[dividend.mag.size + 1] = c shl shift
        }
    }

    val remLen = remArr.lastIndex
    val quotLen = remLen - sorLen + 1
    val quotArr = IntArray(quotLen)

    val sorHigh = sorArr[0]
    val sorHighLong = sorHigh.getL()
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
            val nChunk = nh.toLong() shl Int.SIZE_BITS or nm.getL()
            if (nChunk >= 0) {
                qhat = (nChunk / sorHighLong).toInt()
                qrem = (nChunk - qhat * sorHighLong).toInt()
            } else {
                val tmp = MutableBigInt.divWord(nChunk, sorHigh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr Int.SIZE_BITS).toInt()
            }
        }
        if (qhat == 0) return@forEach
        if (!skipCorrection) {
            val nl = remArr[idx + 2].getL()
            var rs = qrem.getL() shl Int.SIZE_BITS or nl
            var estProd = sorLow.getL() * qhat.getL()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.getL() + sorHighLong).toInt()
                if (qrem.getL() >= sorHighLong) {
                    estProd -= sorLow.getL()
                    rs = qrem.getL() shl Int.SIZE_BITS or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[idx] = 0
        val borrow = mulSub(remArr, sorArr, qhat, sorLen, idx)

        if (borrow + -0x80000000 > nh2) {
            MutableBigInt.divAdd(sorArr, remArr, idx + 1)
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
        val nChunk = nh.toLong() shl Int.SIZE_BITS or nm.getL()
        if (nChunk >= 0) {
            qhat = (nChunk / sorHighLong).toInt()
            qrem = (nChunk - qhat * sorHighLong).toInt()
        } else {
            val tmp = MutableBigInt.divWord(nChunk, sorHigh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr Int.SIZE_BITS).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) {
            val nl = remArr[quotLen + 1].getL()
            var rs = qrem.getL() shl Int.SIZE_BITS or nl
            var estProd = sorLow.getL() * qhat.getL()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.getL() + sorHighLong).toInt()
                if (qrem.getL() >= sorHighLong) {
                    estProd -= sorLow.getL()
                    rs = qrem.getL() shl Int.SIZE_BITS or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[quotLen - 1] = 0
        val borrow = MutableBigInt.mulSub(remArr, sorArr, qhat, sorLen, quotLen - 1)

        if (borrow + -0x80000000 > nh2) {
            MutableBigInt.divAdd(sorArr, remArr, quotLen)
            qhat--
        }

        quotArr[quotLen - 1] = qhat
    }

    return Pair(
        emptyMutableBigIntOf(quotArr),
        emptyMutableBigIntOf(if (shift > 0) MutableBigInt.rightShift(remArr, shift) else remArr)
    )
}


internal fun MutableBigInt.Companion.divWord(n: Long, d: Int): Long {
    val dLong = d.getL()
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

internal fun MutableBigInt.Companion.rightShift(value: IntArray, n: Int): IntArray {
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

internal fun MutableBigInt.Companion.primitiveRightShift(value: IntArray, n: Int): IntArray {
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

internal fun MutableBigInt.Companion.primitiveLeftShift(value: IntArray, n: Int): IntArray {
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

internal fun MutableBigInt.Companion.copyAndShift(
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

internal fun MutableBigInt.Companion.mulSub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    var carry: Long = 0
    (len - 1 downTo 0).forEach { idx ->
        val prod: Long = a[idx].getL() * x.getL() + carry
        val diff = q[offset + idx + 1] - prod
        q[offset + idx + 1] = diff.toInt()
        carry = (prod ushr Int.SIZE_BITS) + (
                if ((diff and 0xffffffffL) > (prod.inv() and 0xffffffffL)) 1 else 0)
    }
    return carry.toInt()
}

internal fun MutableBigInt.Companion.divAdd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0
    (a.lastIndex downTo 0).forEach { idx ->
        val sum: Long = a[idx].getL() + result[idx + offset].getL() + carry
        result[idx + offset] = sum.toInt()
        carry = sum ushr Int.SIZE_BITS
    }
    return carry.toInt()
}