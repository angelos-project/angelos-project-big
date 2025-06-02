package org.angproj.big

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes


public object FuzzerTestBitKt {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider): Unit = withLogic {
        val f1 = data.consumeBytes(64)
        val f2 = data.consumeByte().toInt()

        val r1 = try {
            bigIntOf(f1).testBit(f2).toString()
        } catch (_: BigMathException) {
            ""
        }

        val r2 = try {
            BigInteger(f1).testBit(f2).toString()
        } catch (_: NumberFormatException) {
            ""
        } catch (_: ArithmeticException) {
            ""
        }

        assertEquals(r1, r2)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${2.minutes.inWholeSeconds}"
        ))
    }
}