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
 *      Josh Bloch - earlier implementation
 *      Michael McCloskey - earlier implementation
 *      Alan Eliasen - earlier implementation
 *      Timothy Buktu - earlier implementation
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.big

public infix fun BigInt.and(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = getIdx(this@and, idx) and getIdx(value, idx)
        }
    }
    return fromIntArray(result)
}