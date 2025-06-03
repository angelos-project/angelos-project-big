package org.angproj.big

import kotlin.time.Duration.Companion.seconds

public abstract class FuzzPrefs {
    public val maxTotalTime: Long = 10.seconds.inWholeSeconds //2.minutes.inWholeSeconds
}