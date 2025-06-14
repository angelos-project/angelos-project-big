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

import org.angproj.big.newbig.*


public operator fun BigInt.div(other: BigInt): BigInt = divide(other)

public operator fun BigInt.rem(other: BigInt): BigInt = remainder(other)

public fun BigInt.divide(value: BigInt): BigInt = divideAndRemainder(value).first

public fun BigInt.remainder(value: BigInt): BigInt = divideAndRemainder(value).second

/*public fun BigInt.divideAndRemainder0(
    value: BigInt
): Pair<BigInt, BigInt> = when {
    value.sigNum.isZero() -> error { BigMathException("Divisor can not be zero.") }
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
                    value.mag.size == 1 -> BigInt.innerDivideOneWord(qabs, vabs)
                    else -> BigInt.innerDivideMagnitude(qabs, vabs)
                }
                val q = toComplementedIntArray(result.first)
                val r = stripLeadingZeros(toComplementedIntArray(result.second))
                Pair(
                    BigInt.raw<Unit>(q, if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                    BigInt.raw<Unit>(r, sigNumZeroAdjust(r, this.sigNum))
                )
            }
        }
    }
}*/

public fun BigInt.divideAndRemainder(
    value: BigInt, needRemainder: Boolean = true
): Pair<BigInt, BigInt> {
    return when {
        value.sigNum.isZero() -> error { BigMathException("Divisor can not be zero.") }
        sigNum.isZero() -> Pair(BigInt.zero, BigInt.zero)
        value.mag.size == 1 -> BigInt.divideOneWord(mag, value.mag).let {
            Pair(
                ExportImportBigInt.internalOf(it.first, if (sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                if(needRemainder) ExportImportBigInt.internalOf(it.second, sigNum) else BigInt.nullObject
            )
        }
        else -> {
            val dividend = this //ExportImportBigInt.asComplemented(this.abs()) //.abs()
            val divisor = value //ExportImportBigInt.asComplemented(value.abs()) //.abs()
            val cmp = BigInt.innerCompareMagnitude(dividend.mag, divisor.mag)
            when {
                cmp.isLesser() -> Pair(BigInt.zero, this)
                cmp.isEqual() -> Pair(BigInt.one, BigInt.zero)
                else -> BigInt.divideMagnitude(
                    dividend.mag,
                    divisor.mag,
                    needRemainder).let {
                        val quotSig = if (sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE
                        val quotient = it.first
                        val remainder = it.second // ExportImportBigInt.asComplemented(it.second, sigNum)
                    Pair(
                        ExportImportBigInt.externalOf(quotSig, quotient),
                        if(needRemainder) ExportImportBigInt.externalOf(sigNum, remainder) else BigInt.nullObject
                    )
                }
            }
        }
    }
}

public fun divWord(n: Long, d: Int): Long{
    val dLong = d.longMask()
    var r: Long
    var q: Long
    if (dLong == 1L) {
        q = n.toInt().toLong()
        r = 0
        return (r shl 32) or q.lowerHalf()
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
    return (r shl 32) or q.lowerHalf()
}

public fun BigInt.Companion.divideOneWord(
    dividend: IntArray, divisor: IntArray
): Pair<IntArray, IntArray> {
    val divisorLong: Long = divisor.last().longMask()
    val divisorInt: Int = divisorLong.toInt()

    // Special case of one word dividend
    if (dividend.size == 1) {
        val dividendValue: Long = dividend.last().longMask()
        val q = (dividendValue / divisorLong).toInt()
        val r = (dividendValue - q * divisorLong).toInt()
        return Pair(intArrayOf(q), intArrayOf(r))
    }

    val quotient = IntArray(dividend.size)

    val shift: Int = divisorInt.countLeadingZeroBits()
    var rem: Int = dividend[0]
    var remLong: Long = rem.longMask()

    if (remLong < divisorLong) {
        quotient[0] = 0
    } else {
        quotient[0] = (remLong / divisorLong).toInt()
        rem = (remLong - (quotient[0] * divisorLong)).toInt()
        remLong = rem.longMask()
    }

    (dividend.lastIndex downTo 1).forEach {
        val dividendEstimate: Long = (remLong shl 32) or dividend.intGet(it-1).longMask()
        val q: Int
        if (dividendEstimate >= 0) {
            q = (dividendEstimate / divisorLong).toInt()
            rem = (dividendEstimate - q * divisorLong).toInt()
        } else {
            val tmp: Long = divWord(dividendEstimate, divisorInt)
            q = tmp.intMask()
            rem = (tmp ushr 32).toInt()
        }
        quotient.intSet(it-1, q)
        remLong = rem.longMask()
    }

    return Pair(
        ExportImportBigInt.trustedStripLeadingZeroInts(quotient),
        intArrayOf(if (shift > 0) rem % divisorInt else rem)
    )
}



/*internal fun BigInt.Companion.innerDivideOneWord0(
    dividend: BigInt,
    divisor: BigInt,
): Pair<BigInt, BigInt> = withLogic {
    val sorLong = getIdxL(divisor, divisor.mag.lastIndex)
    val sorInt = sorLong.toInt()

    if (dividend.mag.size == 1) {
        val dendValue: Long = getIdxL(dividend, dividend.mag.lastIndex)
        val q = (dendValue / sorLong)
        val r = (dendValue - q * sorLong)
        return@withLogic Pair(
            fromLong(q),
            fromLong(r),
        )
    }
    val quotient = emptyBigIntOf(IntArray(dividend.mag.size))

    val shift: Int = sorInt.countLeadingZeroBits()
    var rem: Int = getUnreversedIdx(dividend, 0)
    var remLong = rem.getL()
    if (remLong < sorLong) {
        setUnreversedIdx(quotient.mag, 0, 0)
    } else {
        setUnreversedIdxL(quotient.mag, 0, remLong / sorLong)
        rem = (remLong - getUnreversedIdx(quotient, 0) * sorLong).toInt()
        remLong = rem.getL()
    }

    (dividend.mag.lastIndex downTo 1).forEach { idx ->
        val dendEst = remLong shl TypeBits.int or getIdxL(dividend, idx - 1)
        var q: Int
        if (dendEst >= 0) {
            q = (dendEst / sorLong).toInt()
            rem = (dendEst - q * sorLong).toInt()
        } else {
            val tmp = BigInt.innerDivWord(dendEst, sorInt)
            q = (tmp and 0xffffffffL).toInt()
            rem = (tmp ushr TypeBits.int).toInt()
        }
        quotient.mag.revSet(idx - 1, q)
        remLong = rem.getL()
    }

    return@withLogic Pair(
        quotient,
        fromLong((if(shift > 0) rem % sorInt else rem).toLong())
    )
}

/*internal fun BigInt.Companion.innerDivideOneWord(
    dividend: BigInt,
    divisor: BigInt,
): Pair<BigInt, BigInt> = withLogic {
    val sorLong = getIdxL(divisor, divisor.mag.lastIndex)
    val sorInt = sorLong.toInt()

    if (dividend.mag.size == 1) {
        val dendValue: Long = getIdxL(dividend, dividend.mag.lastIndex)
        val q = (dendValue / sorLong)
        val r = (dendValue - q * sorLong)
        return@withLogic Pair(
            fromLong(q),
            fromLong(r),
        )
    }
    val quotient = IntArray(dividend.mag.size)

    val shift: Int = sorInt.countLeadingZeroBits()
    var rem: Int = getUnreversedIdx(dividend, 0)
    var remLong = rem.getL()
    if (remLong < sorLong) {
        setUnreversedIdx(quotient, 0, 0)
    } else {
        setUnreversedIdxL(quotient, 0, remLong / sorLong)
        rem = (remLong - getUnreversedIdx(quotient, 0) * sorLong).toInt()
        remLong = rem.getL()
    }

    (dividend.mag.lastIndex downTo 1).forEach { idx ->
        val dendEst = remLong shl TypeBits.int or getIdxL(dividend, idx - 1)
        var q: Int
        if (dendEst >= 0) {
            q = (dendEst / sorLong).toInt()
            rem = (dendEst - q * sorLong).toInt()
        } else {
            val tmp = BigInt.innerDivWord(dendEst, sorInt)
            q = (tmp and 0xffffffffL).toInt()
            rem = (tmp ushr TypeBits.int).toInt()
        }
        quotient.revSet(idx - 1, q)
        remLong = rem.getL()
    }

    return@withLogic Pair(
        quotient,
        fromLong((if(shift > 0) rem % sorInt else rem).toLong())
    )
}*/


internal fun BigInt.Companion.innerDivideMagnitude(
    dividend: BigInt,
    divisor: BigInt,
): Pair<IntArray, IntArray> = withLogic {
    //Pair<BigInt, BigInt> = withLogic {
    val shift = divisor.mag[0].countLeadingZeroBits()

    val sorLen = divisor.mag.size
    val sorArr: IntArray = when {
        shift > 0 -> IntArray(divisor.mag.size).also {
            BigInt.innerCopyAndShift(divisor.mag.copyOf(), 0, divisor.mag.size, it, 0, shift) }
        else -> divisor.mag.copyOfRange(0, divisor.mag.size)
    }
    val remArr: IntArray = when {
        shift <= 0 -> IntArray(dividend.mag.size + 1).also { arr ->
            dividend.mag.copyInto(arr, 1, 0, dividend.mag.size)
        }
        dividend.mag[0].countLeadingZeroBits() >= shift -> IntArray(dividend.mag.size + 1).also { arr ->
            BigInt.innerCopyAndShift(dividend.mag.copyOf(), 0, arr.lastIndex, arr, 1, shift)
        }
        else -> IntArray(dividend.mag.size + 2).also { arr ->
            var c = 0
            val n2 = TypeBits.int - shift
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
            val nChunk: Long = nh.toLong() shl TypeBits.int or nm.getL()
            if (nChunk >= 0) {
                qhat = (nChunk / sorHighLong).toInt()
                qrem = (nChunk - qhat * sorHighLong).toInt()
            } else {
                val tmp = BigInt.innerDivWord(nChunk, sorHigh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr TypeBits.int).toInt()
            }
        }

        if (qhat == 0) return@forEach

        if (!skipCorrection) {
            val nl = remArr[idx + 2].getL()
            var rs = qrem.getL() shl TypeBits.int or nl
            var estProd = sorLow.getL() * qhat.getL()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.getL() + sorHighLong).toInt()
                if (qrem.getL() >= sorHighLong) {
                    estProd -= sorLow.getL()
                    rs = qrem.getL() shl TypeBits.int or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[idx] = 0
        val borrow = BigInt.innerMulSub(remArr, sorArr, qhat, sorLen, idx)

        if (borrow + -0x80000000 > nh2) {
            BigInt.innerDivAdd(sorArr, remArr, idx + 1)
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
        val nChunk = nh.toLong() shl TypeBits.int or nm.getL()
        if (nChunk >= 0) {
            qhat = (nChunk / sorHighLong).toInt()
            qrem = (nChunk - qhat * sorHighLong).toInt()
        } else {
            val tmp = BigInt.innerDivWord(nChunk, sorHigh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr TypeBits.int).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) {
            val nl = remArr[quotLen + 1].getL()
            var rs = qrem.getL() shl TypeBits.int or nl
            var estProd = sorLow.getL() * qhat.getL()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.getL() + sorHighLong).toInt()
                if (qrem.getL() >= sorHighLong) {
                    estProd -= sorLow.getL()
                    rs = qrem.getL() shl TypeBits.int or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[quotLen - 1] = 0
        val borrow = BigInt.innerMulSub(remArr, sorArr, qhat, sorLen, quotLen - 1)

        if (borrow + -0x80000000 > nh2) {
            BigInt.innerDivAdd(sorArr, remArr, quotLen)
            qhat--
        }

        quotArr[quotLen - 1] = qhat
    }

    /*return@withLogic Pair(
        emptyBigIntOf(quotArr),
        emptyBigIntOf(if (shift > 0) BigInt.innerRightShift(remArr, shift) else remArr)
    )*/
    return@withLogic Pair(
        quotArr,
        if (shift > 0) BigInt.innerRightShift(remArr, shift) else remArr
    )
}


internal fun BigInt.Companion.innerDivWord(n: Long, d: Int): Long = withLogic{
    val dLong = d.getL()
    var r: Long
    var q: Long
    if (dLong == 1L) {
        q = n.toInt().toLong()
        r = 0
        return@withLogic r shl TypeBits.int or (q and 0xffffffffL)
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
    return@withLogic (r shl TypeBits.int) or (q and 0xffffffffL)
}*/

/*internal fun BigInt.Companion.innerRightShift(value: IntArray, n: Int): IntArray {
    if (value.size == 0) return value
    val nInts = n ushr 5
    val nBits = n and 0x1F
    val value2 = value.copyOf(value.size - nInts)
    if (nBits == 0) return value2
    val bitsInHighWord = TypeBits.int - value2[0].countLeadingZeroBits()
    return if (nBits >= bitsInHighWord) {
        innerPrimitiveLeftShift(value2, TypeBits.int - nBits).copyOf(value.lastIndex)
    } else {
        innerPrimitiveRightShift(value2, nBits)
    }
}

internal fun BigInt.Companion.innerPrimitiveRightShift(value: IntArray, n: Int): IntArray {
    val n2 = TypeBits.int - n
    var c = value[value.lastIndex]
    (value.lastIndex downTo 1).forEach { idx ->
        val b = c
        c = value[idx - 1]
        value[idx] = c shl n2 or (b ushr n)
    }
    value[0] = value[0] ushr n
    return value
}

internal fun BigInt.Companion.innerPrimitiveLeftShift(value: IntArray, n: Int): IntArray {
    val n2 = TypeBits.int - n
    var c = value[0]
    (0 until value.lastIndex).forEach { idx ->
        val b = c
        c = value[idx + 1]
        value[idx] = b shl n or (c ushr n2)
    }
    value[value.lastIndex] = value[value.lastIndex] shl n
    return value
}

internal fun BigInt.Companion.innerCopyAndShift0(
    src: IntArray, srcFrom_: Int, srcLen: Int,
    dst: IntArray, dstFrom: Int, shift: Int
) {
    var srcFrom = srcFrom_
    val n2 = TypeBits.int - shift
    var c = src[srcFrom]
    for (i in 0 until srcLen - 1) {
        val b = c
        c = src[++srcFrom]
        dst[dstFrom + i] = b shl shift or (c ushr n2)
    }
    dst[dstFrom + srcLen - 1] = c shl shift
}

internal fun BigInt.Companion.innerCopyAndShift(
    src: IntArray, srcFrom_: Int, srcLen: Int,
    dst: IntArray, dstFrom: Int, shift: Int
) {
    var srcFrom = srcFrom_
    val n2 = TypeBits.int - shift
    var c = src[srcFrom]
    for (i in 0 until srcLen - 1) {
        val b: Int = c
        c = src[++srcFrom]
        dst[dstFrom + i] = (b shl shift) or (c ushr n2)
    }
    dst[dstFrom + srcLen - 1] = c shl shift
}

internal fun BigInt.Companion.innerMulSub0(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int = withLogic{
    var carry: Long = 0
    (len - 1 downTo 0).forEach { idx ->
        val prod: Long = a[idx].getL() * x.getL() + carry
        val diff = q[offset + idx + 1] - prod
        q[offset + idx + 1] = diff.toInt()
        carry = (prod ushr TypeBits.int) + (
                if ((diff and 0xffffffffL) > (prod.inv() and 0xffffffffL)) 1 else 0)
    }
    return@withLogic carry.toInt()
}

internal fun BigInt.Companion.innerMulSub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int = withLogic{
    var carry: Long = 0
    val xLong: Long = x.getL()
    (len - 1 downTo 0).forEach { idx ->
        val prod: Long = a[idx].getL() * xLong + carry
        val diff: Long = q[offset + idx + 1] - prod
        q[offset + idx + 1] = diff.toInt()
        carry = (prod ushr TypeBits.int) + (
                if ((diff and 0xffffffffL) > (prod.toInt().inv().toLong() and 0xffffffffL)) 1 else 0)
    }
    return@withLogic carry.toInt()
}

internal fun BigInt.Companion.innerDivAdd(a: IntArray, result: IntArray, offset: Int): Int = withLogic{
    var carry: Long = 0
    (a.lastIndex downTo 0).forEach { idx ->
        val sum: Long = a[idx].getL() + result[idx + offset].getL() + carry
        result[idx + offset] = sum.toInt()
        carry = sum ushr TypeBits.int
    }
    return@withLogic carry.toInt()
}*/


/*public fun BigInt.Companion.divideMagnitude(
    dividendOrig: IntArray,
    divisorOrig: IntArray,
    needRemainder: Boolean
): Pair<IntArray, IntArray> {
    // assert div.intLen > 1
    // D1 normalize the divisor
    // Copy divisor value to protect divisor

    val shift: Int = divisorOrig[0].countLeadingZeroBits()

    val divisorSize: Int = divisorOrig.size
    val divisorArr: IntArray

    val remainderArr: IntArray // Remainder starts as dividend with space for a leading zero
    var remainderOffset: Int = 1
    var remainderSize: Int
    if (shift > 0) {
        println("= A =")
        divisorArr = IntArray(divisorSize)
        copyAndShift(divisorOrig, 0, divisorOrig.size, divisorArr, 0, shift)
        if (dividendOrig[0].countLeadingZeroBits() >= shift) {
            println("= B =")
            remainderArr = IntArray(dividendOrig.size + 1) // 1
            remainderSize = dividendOrig.size
            //remainderOffset = 1
            copyAndShift(dividendOrig, 0, dividendOrig.size, remainderArr, 1, shift) // 1
            //println(remainderArr.joinToString(", "))
        } else {
            println("= C =")
            remainderArr = IntArray(dividendOrig.size + 1 + 1) // 2
            remainderSize = dividendOrig.size + 1 // 1
            //remainderOffset = 1
            var rFrom: Int = 0
            var c = 0
            val n2 = 32 - shift
            var i = 1 // 1
            while (i < dividendOrig.size + 1) { // 1
                val b = c
                c = dividendOrig[rFrom++]
                remainderArr[i++] = (b shl shift) or (c ushr n2)
            }
            remainderArr[dividendOrig.size + 1] = c shl shift // 1
            //println(remainderArr.joinToString(", "))
        }
    } else {
        println("= D =")
        divisorArr = divisorOrig.copyOfRange(0, divisorOrig.size)
        remainderArr = IntArray(dividendOrig.size + 1) // 1
        dividendOrig.copyInto(remainderArr, 0, 1, dividendOrig.size) // 1
        remainderSize = dividendOrig.size
        //remainderOffset = 1
    }

    //remainderSize = remainderArr.lastIndex
    val nlen: Int = remainderSize

    val limit = nlen - divisorSize + remainderOffset // 1
    val quotientArr: IntArray = IntArray(limit)


    if (remainderSize == nlen) {
        remainderOffset = 0
        remainderArr[0] = 0
        //remainderSize += 1
    }

    val dh: Int = divisorArr[0]
    val dhLong: Long = dh.longMask()
    val dl: Int = divisorArr[1]

    // D2 Initialize j
    for (j in 0..<limit - 1) {
        // D3 Calculate qhat
        // estimate qhat
        var qhat: Int = 0
        var qrem: Int = 0
        var skipCorrection: Boolean = false
        val nh: Int = remainderArr[j + remainderOffset]
        val nh2: Int = nh + -0x80000000
        val nm: Int = remainderArr[j + 1 + remainderOffset]

        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = ((nh.toLong()) shl 32) or nm.longMask()
            if (nChunk >= 0) {
                qhat = (nChunk / dhLong).toInt()
                qrem = (nChunk - (qhat * dhLong)).toInt()
            } else {
                val tmp: Long = divWord(nChunk, dh)
                qhat = tmp.intMask()
                qrem = (tmp ushr 32).toInt()
            }
        }

        if (qhat == 0) continue

        if (!skipCorrection) { // Correct qhat
            val nl: Long = remainderArr[j + 2 + remainderOffset].longMask()
            var rs: Long = (qrem.longMask() shl 32) or nl
            var estProduct: Long = dl.longMask() * qhat.longMask()

            if (estProduct.longCmp(rs)) {
                qhat--
                qrem = (qrem.longMask() + dhLong).toInt()
                if (qrem.longMask() >= dhLong) {
                    estProduct -= dl.longMask()
                    rs = (qrem.longMask() shl 32) or nl
                    if (estProduct.longCmp(rs)) qhat--
                }
            }
        }

        // D4 Multiply and subtract
        remainderArr[j + remainderOffset] = 0
        val borrow = mulSub(remainderArr, divisorArr, qhat, divisorSize, j + remainderOffset)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            divAdd(divisorArr, remainderArr, j + 1 + remainderOffset)
            qhat--
        }

        // Store the quotient digit
        quotientArr[j] = qhat
    } // D7 loop on j

    println("remainderArr: ${remainderArr.size}, limit: $limit, 1, remainderOffset: $remainderOffset")
    // D3 Calculate qhat
    // estimate qhat
    var qhat: Int = 0
    var qrem: Int = 0
    var skipCorrection = false
    val nh: Int = remainderArr[limit - 1 + remainderOffset]
    val nh2: Int = nh + -0x80000000
    val nm: Int = remainderArr[limit + remainderOffset]

    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = (nh.toLong() shl 32) or nm.longMask()
        if (nChunk >= 0) {
            qhat = (nChunk / dhLong).toInt()
            qrem = (nChunk - (qhat * dhLong)).toInt()
        } else {
            val tmp = divWord(nChunk, dh)
            qhat = tmp.intMask()
            qrem = (tmp ushr 32).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) { // Correct qhat
            val nl: Long = remainderArr[limit + 1 + remainderOffset].longMask()
            var rs = (qrem.longMask() shl 32) or nl
            var estProduct = dl.longMask() * qhat.longMask()

            if (estProduct.longCmp(rs)) {
                qhat--
                qrem = (qrem.longMask() + dhLong).toInt()
                if (qrem.longMask() >= dhLong) {
                    estProduct -= dl.longMask()
                    rs = (qrem.longMask() shl 32) or nl
                    if (estProduct.longCmp(rs)) qhat--
                }
            }
        }
        remainderArr[limit - 1 + remainderOffset] = 0


        // D4 Multiply and subtract
        val borrow = if (needRemainder) mulSub(remainderArr, divisorArr, qhat, divisorSize, limit - 1 + remainderOffset)
        else mulSubBorrow(remainderArr, divisorArr, qhat, divisorSize, limit - 1 + remainderOffset)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            if (needRemainder) divAdd(divisorArr, remainderArr, limit - 1 + 1 + remainderOffset)
            qhat--
        }

        // Store the quotient digit
        quotientArr[limit - 1] = qhat
    }

    val anOffset = remainderArr.size - remainderSize
    if (needRemainder && shift > 0) rightShift(shift, remainderOffset, remainderArr.size, remainderArr)

    //println(remainderSize)
    //println(remainderArr.size)
    return Pair(
        ExportImportBigInt.trustedStripLeadingZeroInts(quotientArr),
        if(needRemainder) ExportImportBigInt.trustedStripLeadingZeroInts(remainderArr.copyOfRange(0, remainderSize)) else intArrayOf()
    )
}*/

public fun Long.longCmp(other: Long): Boolean = (this + Long.MIN_VALUE) > (other + Long.MIN_VALUE)


public fun rightShift(n: Int, offset: Int, _size: Int, mag: IntArray): Int {
    var size = _size
    if (size == 0) return _size
    val nInts = n ushr 5
    val nBits = n and 0x1F
    size -= nInts
    if (nBits == 0) return size // size or _size
    val bitsInHighWord: Int = LoadAndSaveBigInt.bitLengthForInt(mag[offset]) // lastIndex ??
    if (nBits >= bitsInHighWord) {
        primitiveLeftShift(32 - nBits, offset, size, mag)
        size -= 1
    } else {
        primitiveRightShift(nBits, offset, size, mag)
    }
    return size
}


public fun primitiveRightShift(n: Int, offset: Int, size: Int, mag: IntArray) {
    val n2 = 32 - n
    var i: Int = offset + size - 1
    var c = mag[i] // lastIndex ??
    while (i > offset) {
        val b = c
        c = mag[i - 1]
        mag[i--] = (c shl n2) or (b ushr n)
        //i--
    }
    mag[offset] = mag[offset] ushr n
}


private fun primitiveLeftShift(n: Int, offset: Int, size: Int, mag: IntArray) {
    val n2 = 32 - n
    var i: Int = offset
    var c = mag[i]
    val m: Int = i + size - 1
    while (i < m) {
        val b = c
        c = mag[i + 1]
        mag[i] = (b shl n) or (c ushr n2)
        i++
    }
    mag[offset + size - 1] = mag[offset + size - 1] shl n
}


public fun divAdd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0

    a.indices.reversed().forEach { j ->
        val sum: Long = a[j].longMask() + result[j + offset].longMask() + carry
        result[j + offset] = sum.toInt()
        carry = sum ushr 32
    }
    return carry.toInt()
}

public fun mulSub(q: IntArray, a: IntArray, x: Int, len: Int, _offset: Int): Int {
    var offset = _offset
    val xLong: Long = x.longMask()
    var carry: Long = 0
    offset += len

    (len - 1 downTo 0).forEach { j ->
        val product: Long = a[j].longMask() * xLong + carry
        val difference: Long = q[offset] - product
        q[offset--] = difference.toInt()
        carry = (product ushr 32) + when {
            difference.lowerHalf() > product.toInt().inv().longMask() -> 1
            else -> 0
        }
    }
    return carry.toInt()
}

public fun mulSubBorrow(q: IntArray, a: IntArray, x: Int, len: Int, _offset: Int): Int {
    var offset = _offset
    val xLong: Long = x.longMask()
    var carry: Long = 0
    offset += len

    (len - 1 downTo 0).forEach { j ->
        val product: Long = a[j].longMask() * xLong + carry
        val difference: Long = q[offset--] - product
        carry = (product ushr 32) + when {
            difference.lowerHalf() > product.toInt().inv().longMask() -> 1
            else -> 0
        }
    }
    return carry.toInt()
}

public fun copyAndShift(src: IntArray, _srcFrom: Int, srcLen: Int, dst: IntArray, dstFrom: Int, shift: Int) {
    var srcFrom = _srcFrom
    val n2 = 32 - shift
    var c = src[srcFrom]

    (0..<srcLen - 1).forEach { i ->
        val b = c
        c = src[++srcFrom]
        dst[dstFrom + i] = (b shl shift) or (c ushr n2)
    }
    dst[dstFrom + srcLen - 1] = c shl shift
}


public fun BigInt.Companion.divideMagnitude(
    dividendOrig: IntArray,
    divisorOrig: IntArray,
    needRemainder: Boolean
): Pair<IntArray, IntArray> {
    // NEW
    var remOffset: Int
    var remSize: Int

    val dividendOrigOffset = 0
    val divisorOrigOffset = 0

    // assert div.intLen > 1
    // D1 normalize the divisor
    val shift: Int = divisorOrig[divisorOrigOffset].countLeadingZeroBits()
    // Copy divisor value to protect divisor
    val dlen: Int = divisorOrig.size
    val divisor: IntArray
    var rem: IntArray // Remainder starts as dividend with space for a leading zero
    if (shift > 0) {
        divisor = IntArray(dlen)
        copyAndShift(divisorOrig, divisorOrigOffset, dlen, divisor, 0, shift)
        if (dividendOrig[dividendOrigOffset].countLeadingZeroBits() >= shift) {
            rem = IntArray(dividendOrig.size + 1)
            //rem = MutableBigInteger(remarr)
            remSize = dividendOrig.size
            remOffset = 1
            copyAndShift(dividendOrig, dividendOrigOffset, dividendOrig.size, rem, 1, shift)
        } else {
            rem = IntArray(dividendOrig.size + 2)
            //rem = MutableBigInteger(remarr)
            remSize = dividendOrig.size + 1
            remOffset = 1
            var rFrom: Int = dividendOrigOffset
            var c = 0
            val n2 = 32 - shift
            var i = 1
            while (i < dividendOrig.size + 1) {
                val b = c
                c = dividendOrig[rFrom]
                rem[i] = (b shl shift) or (c ushr n2)
                i++
                rFrom++
            }
            rem[dividendOrig.size + 1] = c shl shift
        }
    } else {
        divisor = divisorOrig.copyOfRange(divisorOrigOffset, divisorOrigOffset + divisorOrig.size)
        rem = IntArray(dividendOrig.size + 1)
        dividendOrig.copyInto(rem, dividendOrigOffset, 1, dividendOrig.size)
        remSize = dividendOrig.size
        remOffset = 1
    }

    val nlen: Int = remSize

    // Set the quotient size
    val limit = nlen - dlen + 1
    /*if (quotient.value.length < limit) {
        quotient.value = IntArray(limit)
        quotient.offset = 0
    }
    quotient.intLen = limit*/
    val q: IntArray = IntArray(limit) // quotient.value


    // Must insert leading 0 in rem if its length did not change
    if (remSize == nlen) {
        remOffset = 0
        rem[0] = 0
        remSize += 1
    }

    val dh: Int = divisor[0]
    val dhLong: Long = dh.longMask()
    val dl: Int = divisor[1]

    // D2 Initialize j
    for (j in 0..<limit - 1) {
        // D3 Calculate qhat
        // estimate qhat
        var qhat: Int
        var qrem: Int
        var skipCorrection: Boolean = false
        val nh: Int = rem[j + remOffset]
        val nh2: Int = nh + -0x80000000
        val nm: Int = rem[j + 1 + remOffset]

        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk: Long = (nh.toLong() shl 32) or nm.longMask()
            if (nChunk >= 0) {
                qhat = (nChunk / dhLong).toInt()
                qrem = (nChunk - (qhat * dhLong)).toInt()
            } else {
                val tmp = divWord(nChunk, dh)
                qhat = tmp.intMask()
                qrem = (tmp ushr 32).toInt()
            }
        }

        if (qhat == 0) continue

        if (!skipCorrection) { // Correct qhat
            val nl: Long = rem[j + 2 + remOffset].longMask()
            var rs: Long = (qrem.longMask() shl 32) or nl
            //var rs = ((qrem.longMask() shl 32) or nl.toInt()).toLong()
            var estProduct: Long = dl.longMask() * qhat.longMask()

            if (estProduct.longCmp(rs)) {
                qhat--
                qrem = (qrem.longMask() + dhLong).toInt()
                //qrem = (qrem.longMask() + dhLong) as Int
                if (qrem.longMask() >= dhLong) {
                    estProduct -= dl.longMask()
                    rs = (qrem.longMask() shl 32) or nl
                    //rs = ((qrem.longMask() shl 32) or nl.toInt()).toLong()
                    if (estProduct.longCmp(rs)) qhat--
                }
            }
        }

        // D4 Multiply and subtract
        rem[j + remOffset] = 0
        val borrow: Int = mulSub(rem, divisor, qhat, dlen, j + remOffset)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            divAdd(divisor, rem, j + 1 + remOffset)
            qhat--
        }

        // Store the quotient digit
        q[j] = qhat
    } // D7 loop on j

    // D3 Calculate qhat
    // estimate qhat
    var qhat: Int = 0
    var qrem: Int = 0
    var skipCorrection: Boolean = false
    val nh: Int = rem[limit - 1 + remOffset]
    val nh2: Int = nh + -0x80000000
    val nm: Int = rem[limit + remOffset]

    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk: Long = ((nh.toLong()) shl 32) or nm.longMask()
        if (nChunk >= 0) {
            qhat = (nChunk / dhLong).toInt()
            qrem = (nChunk - (qhat * dhLong)).toInt()
        } else {
            val tmp = divWord(nChunk, dh)
            qhat = tmp.intMask()
            qrem = (tmp ushr 32).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) { // Correct qhat
            val nl: Long = rem[limit + 1 + remOffset].longMask()
            var rs: Long = (qrem.longMask() shl 32) or nl
            //var rs = ((qrem.longMask() shl 32) or nl.toInt())
            var estProduct: Long = dl.longMask() * qhat.longMask()

            if (estProduct.longCmp(rs)) {
                qhat--
                qrem = (qrem.longMask() + dhLong).toInt()
                // qrem = (qrem.longMask() + dhLong) as Int
                if (qrem.longMask() >= dhLong) {
                    estProduct -= dl.longMask()
                    rs = (qrem.longMask() shl 32) or nl
                    //rs = ((qrem.longMask() shl 32) or nl.toInt())
                    if (estProduct.longCmp(rs)) qhat--
                }
            }
        }
        rem[limit - 1 + remOffset] = 0


        // D4 Multiply and subtract
        val borrow = if (needRemainder) mulSub(rem, divisor, qhat, dlen, limit - 1 + remOffset)
        else mulSubBorrow(rem, divisor, qhat, dlen, limit - 1 + remOffset)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            if (needRemainder) divAdd(divisor, rem, limit - 1 + 1 + remOffset)
            qhat--
        }

        // Store the quotient digit
        q[limit - 1] = qhat
    }


    if (needRemainder) {
        // D8 Unnormalize
        if (shift > 0) rightShift(shift, remOffset, remSize, rem)
        rem = rem.copyOfRange(remOffset, if(rem.last() == 0) remSize -1 else remSize)

        return Pair(
            ExportImportBigInt.trustedStripLeadingZeroInts(q),
            ExportImportBigInt.trustedStripLeadingZeroInts(rem)
        )
    }

    return Pair(
        ExportImportBigInt.trustedStripLeadingZeroInts(q),
        intArrayOf()
    )
}


/*
private fun divideMagnitude(
    div: MutableBigInteger,
    quotient: MutableBigInteger,
    needRemainder: Boolean
): MutableBigInteger? {
    // assert div.intLen > 1
    // D1 normalize the divisor
    val shift: Int = java.lang.Integer.numberOfLeadingZeros(div.value.get(div.offset))
    // Copy divisor value to protect divisor
    val dlen: Int = div.intLen
    val divisor: IntArray
    val rem: MutableBigInteger // Remainder starts as dividend with space for a leading zero
    if (shift > 0) {
        divisor = IntArray(dlen)
        copyAndShift(div.value, div.offset, dlen, divisor, 0, shift)
        if (java.lang.Integer.numberOfLeadingZeros(value.get(offset)) >= shift) {
            val remarr = IntArray(intLen + 1)
            rem = MutableBigInteger(remarr)
            rem.intLen = intLen
            rem.offset = 1
            copyAndShift(value, offset, intLen, remarr, 1, shift)
        } else {
            val remarr = IntArray(intLen + 2)
            rem = MutableBigInteger(remarr)
            rem.intLen = intLen + 1
            rem.offset = 1
            var rFrom: Int = offset
            var c = 0
            val n2 = 32 - shift
            var i = 1
            while (i < intLen + 1) {
                val b = c
                c = value.get(rFrom)
                remarr[i] = (b shl shift) or (c ushr n2)
                i++
                rFrom++
            }
            remarr[intLen + 1] = c shl shift
        }
    } else {
        divisor = java.util.Arrays.copyOfRange<Any>(div.value, div.offset, div.offset + div.intLen)
        rem = MutableBigInteger(IntArray(intLen + 1))
        java.lang.System.arraycopy(value, offset, rem.value, 1, intLen)
        rem.intLen = intLen
        rem.offset = 1
    }

    val nlen: Int = rem.intLen

    // Set the quotient size
    val limit = nlen - dlen + 1
    if (quotient.value.length < limit) {
        quotient.value = IntArray(limit)
        quotient.offset = 0
    }
    quotient.intLen = limit
    val q: IntArray = quotient.value


    // Must insert leading 0 in rem if its length did not change
    if (rem.intLen === nlen) {
        rem.offset = 0
        rem.value.get(0) = 0
        rem.intLen++
    }

    val dh = divisor[0]
    val dhLong = (dh and LONG_MASK).toLong()
    val dl = divisor[1]

    // D2 Initialize j
    for (j in 0..<limit - 1) {
        // D3 Calculate qhat
        // estimate qhat
        var qhat = 0
        var qrem = 0
        var skipCorrection = false
        val nh: Int = rem.value.get(j + rem.offset)
        val nh2 = nh + -0x80000000
        val nm: Int = rem.value.get(j + 1 + rem.offset)

        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = ((nh.toLong()) shl 32) or ((nm and LONG_MASK).toLong())
            if (nChunk >= 0) {
                qhat = (nChunk / dhLong).toInt()
                qrem = (nChunk - (qhat * dhLong)).toInt()
            } else {
                val tmp = divWord(nChunk, dh)
                qhat = (tmp and LONG_MASK) as Int
                qrem = (tmp ushr 32).toInt()
            }
        }

        if (qhat == 0) continue

        if (!skipCorrection) { // Correct qhat
            val nl: Long = rem.value.get(j + 2 + rem.offset) and LONG_MASK
            var rs = (((qrem and LONG_MASK) shl 32) or nl.toInt()).toLong()
            var estProduct = ((dl and LONG_MASK) * (qhat and LONG_MASK)).toLong()

            if (longCmp(estProduct, rs)) {
                qhat--
                qrem = ((qrem and LONG_MASK) + dhLong) as Int
                if ((qrem and LONG_MASK) >= dhLong) {
                    estProduct -= (dl and LONG_MASK)
                    rs = (((qrem and LONG_MASK) shl 32) or nl.toInt()).toLong()
                    if (longCmp(estProduct, rs)) qhat--
                }
            }
        }

        // D4 Multiply and subtract
        rem.value.get(j + rem.offset) = 0
        val borrow = mulsub(rem.value, divisor, qhat, dlen, j + rem.offset)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            divadd(divisor, rem.value, j + 1 + rem.offset)
            qhat--
        }

        // Store the quotient digit
        q[j] = qhat
    } // D7 loop on j

    // D3 Calculate qhat
    // estimate qhat
    var qhat = 0
    var qrem = 0
    var skipCorrection = false
    val nh: Int = rem.value.get(limit - 1 + rem.offset)
    val nh2 = nh + -0x80000000
    val nm: Int = rem.value.get(limit + rem.offset)

    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = ((nh.toLong()) shl 32) or ((nm and LONG_MASK).toLong())
        if (nChunk >= 0) {
            qhat = (nChunk / dhLong).toInt()
            qrem = (nChunk - (qhat * dhLong)).toInt()
        } else {
            val tmp = divWord(nChunk, dh)
            qhat = (tmp and LONG_MASK) as Int
            qrem = (tmp ushr 32).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) { // Correct qhat
            val nl: Long = rem.value.get(limit + 1 + rem.offset) and LONG_MASK
            var rs = (((qrem and LONG_MASK) shl 32) or nl.toInt()).toLong()
            var estProduct = ((dl and LONG_MASK) * (qhat and LONG_MASK)).toLong()

            if (longCmp(estProduct, rs)) {
                qhat--
                qrem = ((qrem and LONG_MASK) + dhLong) as Int
                if ((qrem and LONG_MASK) >= dhLong) {
                    estProduct -= (dl and LONG_MASK)
                    rs = (((qrem and LONG_MASK) shl 32) or nl.toInt()).toLong()
                    if (longCmp(estProduct, rs)) qhat--
                }
            }
        }
        rem.value.get(limit - 1 + rem.offset) = 0


        // D4 Multiply and subtract
        val borrow = if (needRemainder) mulsub(rem.value, divisor, qhat, dlen, limit - 1 + rem.offset)
        else mulsubBorrow(rem.value, divisor, qhat, dlen, limit - 1 + rem.offset)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            if (needRemainder) divadd(divisor, rem.value, limit - 1 + 1 + rem.offset)
            qhat--
        }

        // Store the quotient digit
        q[(limit - 1)] = qhat
    }


    if (needRemainder) {
        // D8 Unnormalize
        if (shift > 0) rem.rightShift(shift)
        rem.normalize()
    }
    quotient.normalize()
    return if (needRemainder) rem else null
}

 */