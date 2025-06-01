package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertEquals


public object FuzzerLong: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeLong()

        val r1 = try {
            bigIntOf(f1).toLong()
        } catch (e: BigMathException) {
            byteArrayOf()
        }

        val r2 = try {
            BigInteger.valueOf(f1).toLong()
        } catch (e: NumberFormatException) {
            byteArrayOf()
        }

        assertEquals(r1, r2)
    }
}