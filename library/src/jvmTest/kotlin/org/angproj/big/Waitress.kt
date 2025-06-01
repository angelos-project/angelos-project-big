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
import kotlinx.coroutines.sync.Mutex

public interface Waitress {
    public fun wakeUp(): Boolean
    public val job: Job
}

/**
 * Answers the wakeUp call but is busy until ready.
 * The underlying coroutine has to be cancelled manually to break out of the infinite loop.
 * */
public fun answer(action: suspend CoroutineScope.() -> Unit) = object : Waitress {
    private val mutex: Mutex = Mutex()

    override val job: Job = CoroutineScope(Dispatchers.Default).async {
        while (isActive) {
            sleep()
            action()
        }
    }

    init { job.start() }

    private suspend fun sleep(): Unit = mutex.lock()
    override fun wakeUp(): Boolean = when(mutex.isLocked) {
        false -> false
        else -> { mutex.unlock(); true }
    }
}