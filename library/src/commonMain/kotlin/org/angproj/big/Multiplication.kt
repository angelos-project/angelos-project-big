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
 *      Kristoffer Paulsson - Port to Kotlin and adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits
import org.angproj.big.newbig.ExportImportBigInt
import org.angproj.big.newbig.longMask
import org.angproj.big.newbig.rev

public operator fun BigInt.times(other: BigInt): BigInt = multiply(other)

public fun BigInt.multiply(value: BigInt): BigInt = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> ExportImportBigInt.internalOf(
        ExportImportBigInt.trustedStripLeadingZeroInts(BigInt.innerMultiply(this.mag, value.mag)),
        if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE
    )
}

public fun BigInt.Companion.innerMultiply(x: IntArray, y: IntArray): IntArray {
    val xRev = x.rev(0)
    val yRev = y.rev(0)

    val z = BigInt.innerMultiplication(x, y)
    (xRev - 1 downTo 0).forEach { i ->
        var carry: Long = 0
        (yRev downTo 0).forEach { j ->
            val k = j + i + 1
            (y[j].longMask() * x[i].longMask() + z[k].longMask() + carry).also {
                z[k] = it.toInt()
                carry = it ushr 32
            }
        }
        z[i] = carry.toInt()
    }

    return z
}

public fun BigInt.Companion.innerMultiplication(x: IntArray, y: IntArray): IntArray {
    val xRev = x.rev(0)

    val z = IntArray(x.size + y.size)
    val k = 1 + xRev
    var carry: Long = 0
    (y.lastIndex downTo 0).forEach { j ->
        (y[j].longMask() * x[xRev].longMask() + carry).also {
            z[j + k] = it.toInt()
            carry = it ushr 32
        }
    }
    z[xRev] = carry.toInt()

    return z
}






public fun BigInt.multiply0(value: BigInt): BigInt = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> biggerFirst(this, value) { big, little ->
        val negative = big.sigNum.isNegative().let { if (little.sigNum.isNegative()) !it else it }
        val product = BigInt.innerMultiply0(
            big.abs(),
            little.abs()
        )
        val result = BigInt.raw<Unit>(product.mag.copyOf(), BigSigned.POSITIVE)
        return@biggerFirst if (negative) result.negate() else result
    }
}

internal fun BigInt.Companion.innerMultiply0(x: BigInt, y: BigInt): BigInt = withLogic {
    val result = emptyBigIntOf(IntArray(x.mag.size + y.mag.size))

    result.mag.revSet(x.mag.size, innerMultiply1(result, x, getIdx(y, 0)))
    (1 until y.mag.size).forEach { idy ->
        val num = getIdxL(y, idy)
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += getIdxL(x, idx) * num + getIdxL(result, idy + idx)
            setIdxL(result.mag, idy + idx, carry)
            carry = carry ushr TypeBits.int
        }
        setIdxL(result.mag, idy + x.mag.size, carry)
    }
    return@withLogic result
}

internal fun BigInt.Companion.innerMultiply1(result: BigInt, x: BigInt, y: Int): Int = withLogic {
    val first = y.getL()
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += getIdxL(x, idx) * first
        setIdxL(result.mag, idx, carry)
        carry = carry ushr TypeBits.int
    }
    return@withLogic carry.toInt()
}