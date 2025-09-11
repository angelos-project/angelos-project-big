package org.angproj.big

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


public object FuzzerDivisionSelfKt : FuzzPrefs() {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) {
        val f1 = data.consumeBytes(16)
        val f2 = data.consumeBytes(16)

        try {
            val dividend = bigIntOf(f1)
            val divisor = bigIntOf(f2)

            if(divisor == BigInt.zero) {
                try {
                    dividend.divideAndRemainder(divisor)
                    assertFalse(true)
                } catch (_: BigMathException) {
                    assertTrue(true)
                }
            } else {
                val result = dividend.divideAndRemainder(divisor)

                val quotient = result.first
                val remainder = result.second

                assertEquals(dividend, divisor.times(quotient).plus(remainder))
            }
        } catch (e: BigMathException) {

        }
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${maxTotalTime}"
        ))
    }
}