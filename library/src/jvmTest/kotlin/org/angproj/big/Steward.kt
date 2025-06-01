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
import kotlinx.coroutines.sync.Semaphore

public interface Steward {
    public fun wakeUp(): Int
    public val job: Job
}

/**
 * Attends to every wakeUp for a specified number of times.
 * The underlying coroutine has to be cancelled manually to break out of the infinite loop.
 * */
public fun attend(maxCount: Int = Int.MAX_VALUE, action: suspend CoroutineScope.() -> Unit) = object : Steward {
    init { require(maxCount > 1) }
    private val semaphore: Semaphore = Semaphore(maxCount)

    override val job: Job = CoroutineScope(Dispatchers.Default).async {
        while (isActive) {
            sleep()
            action()
        }
    }

    init { job.start() }

    private suspend fun sleep(): Unit = semaphore.release()
    override fun wakeUp(): Int = when(semaphore.availablePermits) {
        0 -> Unit
        else -> semaphore.release()
    }.let { semaphore.availablePermits }
}