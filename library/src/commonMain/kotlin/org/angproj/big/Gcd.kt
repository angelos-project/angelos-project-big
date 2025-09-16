/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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


public fun BigInt.gcd(b: BigInt): BigInt = BigInt.innerGcd(this, b)

internal fun BigInt.Companion.innerGcd(a: BigInt, b: BigInt): BigInt {
    if (b.compareSpecial(BigInt.zero).isEqual())
        return a
    return innerGcd(b, a.mod(b));
}