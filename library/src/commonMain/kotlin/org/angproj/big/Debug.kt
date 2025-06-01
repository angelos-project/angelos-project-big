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

import org.angproj.aux.util.BinHex

public fun BigInt.printDebug() {
    println("Value: $this")
    println("Hex value: " + BinHex.encodeToHex(toByteArray()))
}

public fun BigInt.printDebug(title: String) {
    println("==== ==== $title ==== ====")
    printDebug()
    println()
}