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

import kotlinx.coroutines.*
import kotlin.time.*


public fun clock(
    unit: DurationUnit, ticks: Int, action: suspend CoroutineScope.() -> Unit
) = CoroutineScope(Dispatchers.Default).async {
    var start = TimeSource.Monotonic.markNow()
    var counter: Long = 0

    while (isActive) {
        action()

        counter++
        val elapsed = start.elapsedNow()

        if(counter == Long.MAX_VALUE) {
            start += elapsed
            counter = 0
        }

        delay((counter.toDouble() / ticks).toDuration(unit) - elapsed)
    }
}.apply { start() }
