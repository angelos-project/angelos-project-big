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
 * The function 'implMultiplyToLen' has been licensed under GNU General Public License version 2
 *
 * SPDX-License-Identifier: MIT OR GPL-2.0-only
 *
 * Contributors:
 *      Kristoffer Paulsson - initial adaption
 */
package org.angproj.big.newbig

import org.angproj.big.BigInt
import org.angproj.big.BigSigned


public object MultiplyArithm {

    private const val LONG_MASK: Long = 0xffffffffL

    private fun implMultiplyToLen(x: IntArray, y: IntArray): IntArray {
        val xstart = x.lastIndex
        val ystart = y.lastIndex

        val z = IntArray(x.size + y.size)

        var carry: Long = 0
        var j = ystart
        var k = ystart + 1 + xstart
        while (j >= 0) {
            val product = (y[j].toLong() and LONG_MASK) *
                    (x[xstart].toLong() and LONG_MASK) + carry
            z[k] = product.toInt()
            carry = product ushr 32
            j--
            k--
        }
        z[xstart] = carry.toInt()

        for (i in xstart - 1 downTo 0) {
            carry = 0
            j = ystart
            k = ystart + 1 + i
            while (j >= 0) {
                val product = (y[j].toLong() and LONG_MASK) *
                        (x[i].toLong() and LONG_MASK) +
                        (z[k].toLong() and LONG_MASK) + carry
                z[k] = product.toInt()
                carry = product ushr 32
                j--
                k--
            }
            z[i] = carry.toInt()
        }
        return z
    }

    private fun innerMultiply(x: IntArray, y: IntArray): IntArray {
        val xRev = x.rev(0)
        val yRev = y.rev(0)

        val z = IntArray(x.size + y.size)

        var carry: Long = 0
        val k = 1 + xRev
        (yRev downTo 0).forEach { j ->
            (y[j].longMask() * x[xRev].longMask() + carry).also {
                z[j + k] = it.toInt()
                carry = it ushr 32
            }
        }
        z[xRev] = carry.toInt()

        (xRev - 1 downTo 0).forEach { i ->
            carry = 0
            (yRev downTo 0).forEach { j ->
                val k = j + 1 + i
                (y[j].longMask() * x[i].longMask() + z[k].longMask() + carry).also {
                    z[k] = it.toInt()
                    carry = it ushr 32
                }
            }
            z[i] = carry.toInt()
        }
        return z
    }

    public fun multiply(x: BigInt, y: BigInt): BigInt = ExportImportBigInt.internalOf(
        ExportImportBigInt.trustedStripLeadingZeroInts(innerMultiply(x.mag, y.mag)),
        if (x.sigNum == y.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE
    )
}