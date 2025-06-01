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

import org.angproj.big.newbig.*

public fun BigInt.inv(): BigInt = not()

public fun BigInt.not(): BigInt = BigInt.innerNot(mag, sigNum).valueOf()

public fun BigInt.Companion.innerNot(x: IntArray, xSig: BigSigned,): IntArray {
    val xnz = x.firstNonzero()
    val result = IntArray(x.intLength(xSig))

    result.indices.forEach { result[it] = x.intGetComp(result.rev(it), xSig, xnz).inv() }
    return result
}




public fun BigInt.not1(): BigInt = BooleanArithm.not(this)

public fun BigInt.not0(): BigInt {
    val result = IntArray(mag.size + 1).apply {
        indices.forEach { this[it] = getIdx(this@not0, revIdx(it)).inv() }
    }
    return fromIntArray(result)
}