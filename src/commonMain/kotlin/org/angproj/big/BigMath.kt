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
package org.angproj.big

import org.angproj.aux.buf.IntBuffer


public interface BigMath<E : List<Int>>: MathLogic {
    public val mag: E
    public val sigNum: BigSigned

    public val bitCount: Int
    public val bitLength: Int
    public val firstNonZero: Int

    public fun toBigInt(): BigInt
}

public fun BigMath<*>.equalsCompare(x: Any): Boolean {
    if(x === this) return true
    if(x !is BigMath<*>) return false
    if(sigNum != x.sigNum) return false
    if(mag.size != x.mag.size) return false
    return mag.indices.indexOfFirst { mag[it] != x.mag[it] } == -1
}

public fun BigMath<*>.toByteArray(): ByteArray = toByteArray(mag, sigNum, firstNonZero)