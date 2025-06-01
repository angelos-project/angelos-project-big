package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertContentEquals


public object FuzzerAbs: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeBytes(64)

        val r1 = try {
            bigIntOf(f1).abs().toByteArray()
        } catch (e: BigMathException) {
            byteArrayOf()
        }

        val r2 = try {
            BigInteger(f1).abs().toByteArray()
        } catch (e: NumberFormatException) {
            byteArrayOf()
        }

        assertContentEquals(r1, r2)
    }
}