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
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

public fun BigInt.inv(): BigInt = not()

public fun BigInt.not(): BigInt {
    val result = IntArray(mag.size + 1).apply {
        indices.forEach { this[it] = getIdx(this@not, revIdx(it)).inv() }
    }
    return fromIntArray(result)
}