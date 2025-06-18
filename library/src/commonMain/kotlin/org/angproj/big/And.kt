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

import kotlin.math.max

/**
 * Performs a bitwise AND operation on this [BigInt] and the [value] [BigInt].
 *
 * @param value The [BigInt] to AND with.
 * @return A new [BigInt] representing the result of the AND operation.
 */
public infix fun BigInt.and(value: BigInt): BigInt = BigInt.innerAnd(
    mag, sigNum, value.mag, value.sigNum).valueOf()


public fun BigInt.Companion.innerAnd(x: IntArray, xSig: BigSigned, y: IntArray, ySig: BigSigned): IntArray {
    val xnz = x.firstNonzero()
    val ynz = y.firstNonzero()

    val result = IntArray(max(x.intLength(xSig), y.intLength(ySig)))
    result.indices.forEach {
        val r = result.rev(it)
        result[it] = x.intGetComp(r, xSig, xnz) and y.intGetComp(r, ySig, ynz)
    }

    return result
}