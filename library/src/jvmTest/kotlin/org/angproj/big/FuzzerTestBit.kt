package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertEquals


public object FuzzerTestBit: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeBytes(64)
        val f2 = data.consumeByte().toInt()

        val r1 = try {
            bigIntOf(f1).testBit(f2).toString()
        } catch (e: BigMathException) {
            ""
        }

        val r2 = try {
            BigInteger(f1).testBit(f2).toString()
        } catch (e: NumberFormatException) {
            ""
        } catch (e: ArithmeticException) {
            ""
        }

        assertEquals(r1, r2)
    }
}