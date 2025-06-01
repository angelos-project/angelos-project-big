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
 *      Josh Bloch
 *      Michael McCloskey
 *      Alan Eliasen
 *      Timothy Buktu
 *
 * Contributors:
 *      Kristoffer Paulsson - Port to Kotlin and adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.big.newbig.*
import kotlin.math.max


public infix fun BigInt.xor(value: BigInt): BigInt = BigInt.innerXor(mag, sigNum, value.mag, value.sigNum).valueOf()


public fun BigInt.Companion.innerXor(x: IntArray, xSig: BigSigned, y: IntArray, ySig: BigSigned): IntArray {
    val xnz = x.firstNonzero()
    val ynz = y.firstNonzero()

    val result = IntArray(max(x.intLength(xSig), y.intLength(ySig)))
    result.indices.forEach {
        val r = result.rev(it)
        result[it] = x.intGetComp(r, xSig, xnz) xor y.intGetComp(r, ySig, ynz)
    }

    return result
}


public infix fun BigInt.xor1(value: BigInt): BigInt = BooleanArithm.xor(this, value)

public infix fun BigInt.xor0(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = getIdx(this@xor0, idx) xor getIdx(value, idx)
        }
    }

    return fromIntArray(result)
}