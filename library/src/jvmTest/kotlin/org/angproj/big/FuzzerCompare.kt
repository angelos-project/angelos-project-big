package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertEquals


public object FuzzerCompare: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeBytes(64)
        val f2 = data.consumeBytes(64)

        val r1 = try {
            bigIntOf(f1).compareTo(bigIntOf(f2)).toString()
        } catch (e: BigMathException) {
            ""
        }

        val r2 = try {
            BigInteger(f1).compareTo(BigInteger(f2)).toString()
        } catch (e: NumberFormatException) {
            ""
        }

        assertEquals(r1, r2)
    }
}