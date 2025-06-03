package org.angproj.big

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

public abstract class FuzzPrefs {
    public val maxTotalTime: Long = 1.minutes.inWholeSeconds // 10.seconds.inWholeSeconds
}