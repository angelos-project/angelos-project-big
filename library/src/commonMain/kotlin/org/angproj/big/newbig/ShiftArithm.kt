/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 * Copyright (c) 1996, 2022, Oracle and/or its affiliates.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *      https://spdx.org/licenses/GPL-2.0-only.html
 *
 * The functions 'shiftLeft', 'shiftLeftImplWorker', '*.shiftRightImpl', 'shiftRightImplWorker',
 * and 'javaIncrement' has been licensed under GNU General Public License version 2
 *
 * SPDX-License-Identifier: MIT OR GPL-2.0-only
 *
 * Contributors:
 *      Kristoffer Paulsson - initial adaption
 */
package org.angproj.big.newbig

import org.angproj.big.BigInt


public object ShiftArithm {

    public fun shiftLeft(mag: IntArray, n: Int): IntArray {
        val nInts = n ushr 5
        val nBits = n and 0x1f
        val magLen = mag.size
        val newMag: IntArray

        if (nBits == 0) {
            newMag = IntArray(magLen + nInts).also {
                mag.copyInto(it, 0, 0, magLen) }
            //java.lang.System.arraycopy(mag, 0, newMag, 0, magLen)
            //mag.copyInto(newMag, 0, 0, magLen)
        } else {
            var i = 0
            val nBits2 = 32 - nBits
            val highBits = mag[0] ushr nBits2
            newMag = when {
                highBits != 0 -> IntArray(magLen + nInts + 1).also { it[i++] = highBits }
                else -> IntArray(magLen + nInts)
            }
            val numIter = magLen - 1
            shiftLeftImplWorker(newMag, mag, i, nBits, numIter)
            newMag[numIter + i] = mag[numIter] shl nBits
        }
        return newMag
    }

    private fun shiftLeftImplWorker(newArr: IntArray, oldArr: IntArray, _newIdx: Int, shiftCount: Int, numIter: Int) {
        var newIdx = _newIdx
        val shiftCountRight = 32 - shiftCount
        var oldIdx = 0
        while (oldIdx < numIter) {
            newArr[newIdx++] = (oldArr[oldIdx++] shl shiftCount) or (oldArr[oldIdx] ushr shiftCountRight)
        }
    }

    public fun BigInt.shiftRightImpl1(n: Int): BigInt {
        val nInts = n ushr 5
        val nBits = n and 0x1f
        val magLen: Int = mag.size
        var newMag: IntArray

        // Special case: entire contents shifted off the end
        //if (nInts >= magLen) return (if (sigNum.isNonNegative()) BigInt.zero else negConst.get(1))
        if (nInts >= magLen) return (if (sigNum.isNonNegative()) BigInt.zero else BigInt.minusOne)

        if (nBits == 0) {
            val newMagLen = magLen - nInts
            newMag = mag.copyOf(newMagLen)
        } else {
            var i = 0
            val highBits: Int = mag[0] ushr nBits
            newMag = when {
                highBits != 0 -> IntArray(magLen - nInts).also { it[i++] = highBits }
                else -> IntArray(magLen - nInts - 1)
            }
            val numIter = magLen - nInts - 1
            //java.util.Objects.checkFromToIndex(0, numIter + 1, mag.length)
            //java.util.Objects.checkFromToIndex(i, numIter + i, newMag.size)
            shiftRightImplWorker(newMag, mag, i, nBits, numIter)
        }

        if (sigNum.isNegative()) {
            // Find out whether any one-bits were shifted off the end.
            var onesLost = false
            var i = magLen - 1
            val j = magLen - nInts
            while (i >= j && !onesLost) {
                onesLost = (mag[i] != 0)
                i--
            }
            if (!onesLost && nBits != 0) onesLost = (mag[magLen - nInts - 1] shl (32 - nBits) != 0)
            if (onesLost) newMag = javaIncrement(newMag)
        }

        return ExportImportBigInt.internalOf(newMag, sigNum)
    }

    private fun shiftRightImplWorker(newArr: IntArray, oldArr: IntArray, newIdx: Int, shiftCount: Int, numIter: Int) {
        val shiftCountLeft = 32 - shiftCount
        var idx = numIter
        var nidx = if (newIdx == 0) numIter - 1 else numIter
        while (nidx >= newIdx) {
            newArr[nidx--] = (oldArr[idx--] ushr shiftCount) or (oldArr[idx] shl shiftCountLeft)
        }
    }

    private fun javaIncrement(_value: IntArray): IntArray {
        var value = _value
        var lastSum = 0
        var i = value.size - 1
        while (i >= 0 && lastSum == 0) {
            lastSum = (1.let { value[i] += it; value[i] })
            i--
        }
        if (lastSum == 0) {
            value = IntArray(value.size + 1)
            value[0] = 1
        }
        return value
    }

    public fun BigInt.shiftRightImpl(n: Int): BigInt {
        val nInts = n ushr 5
        val nBits = n and 0x1f
        val magLen: Int = mag.size
        var newMag: IntArray

        if (nInts >= magLen) return (if (sigNum.isNonNegative()) BigInt.zero else BigInt.minusOne)

        val newMagLen = magLen - nInts
        if (nBits == 0) {
            newMag = mag.copyOf(newMagLen)
        } else {
            var i = 0
            val highBits: Int = mag[0] ushr nBits
            newMag = when {
                highBits != 0 -> IntArray(newMagLen).also { it[i++] = highBits }
                else -> IntArray(newMagLen - 1)
            }
            val numIter = newMagLen - 1
            val shiftCountLeft = 32 - nBits
            var idx = numIter
            var nidx = if (i == 0) numIter - 1 else numIter
            while (nidx >= i) {
                newMag[nidx--] = (mag[idx--] ushr nBits) or (mag[idx] shl shiftCountLeft)
            }
        }

        if (sigNum.isNegative()) {
            var onesLost = false
            var i = magLen - 1
            val j = newMagLen
            while (i >= j && !onesLost) { onesLost = mag[i--] != 0 }
            if (!onesLost && nBits != 0) onesLost = (mag[newMagLen - 1] shl (32 - nBits) != 0)
            if (onesLost) {
                onesLost = false
                // DO NOT USE newMagLen BELOW HERE
                var k = newMag.lastIndex
                while (k >= 0 && !onesLost) { newMag[k] += 1; onesLost = newMag[k--] != 0 }
                if (!onesLost) newMag = IntArray(newMag.size + 1).also { it[0] = 1 }
            }
        }

        return ExportImportBigInt.internalOf(newMag, sigNum)
    }


    public fun shiftLeft0(mag: IntArray, n: Int): IntArray {
        val nInts = n ushr 5
        val nBits = n and 0x1f
        val magLen = mag.size
        val newMag: IntArray

        if (nBits == 0) {
            newMag = IntArray(magLen + nInts)
            mag.copyInto(newMag, 0, 0, magLen)
        } else {
            val nBitsRev = 32 - nBits
            val highBits = mag[0] ushr nBitsRev
            val extra = if(highBits != 0) 1 else 0
            newMag = IntArray(magLen + nInts + extra)
            if(extra == 1) newMag[0] = highBits
            val magLast = magLen - 1
            (0 until magLast).forEach {
                newMag[it + extra] = (mag[it] shl nBits) or (mag[it+1] ushr nBitsRev)
            }
            newMag[magLast + extra] = mag[magLast] shl nBits
        }
        return newMag
    }

    private fun shiftLeftImplWorker0(newArr: IntArray, oldArr: IntArray, _newIdx: Int, shiftCount: Int, numIter: Int) {
        var newIdx = _newIdx
        val shiftCountRight = 32 - shiftCount
        var oldIdx = 0
        while (oldIdx < numIter) {
            newArr[newIdx++] = (oldArr[oldIdx++] shl shiftCount) or (oldArr[oldIdx] ushr shiftCountRight)
        }
    }
}