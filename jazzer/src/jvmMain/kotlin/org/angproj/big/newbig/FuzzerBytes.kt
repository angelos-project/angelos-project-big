package org.angproj.big.newbig

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import org.angproj.big.BigMathException
import org.angproj.big.withLogic
import java.math.BigInteger
import kotlin.test.assertContentEquals
import kotlin.time.Duration.Companion.minutes


public object FuzzerBytesKt {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider): Unit = withLogic {
        val f1 = data.consumeBytes(64)

        val r1 = try {
            val b1 = internalOf(f1)
            toByteArray(b1.mag, b1.sigNum)
        } catch (_: BigMathException) {
            byteArrayOf()
        }

        val r2 = try {
            BigInteger(f1).toByteArray()
        } catch (_: NumberFormatException) {
            byteArrayOf()
        }

        assertContentEquals(r1, r2)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${2.minutes.inWholeSeconds}"
        ))
    }
}