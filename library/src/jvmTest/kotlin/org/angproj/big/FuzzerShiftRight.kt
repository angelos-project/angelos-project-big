package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertContentEquals


public object FuzzerShiftRight: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeBytes(14)
        val f2 = data.consumeByte().toInt()

        val r1 = try {
            bigIntOf(f1).shiftRight(f2).toByteArray()
        } catch (e: BigMathException) {
            byteArrayOf()
        }

        val r2 = try {
            BigInteger(f1).shiftRight(f2).toByteArray()
        } catch (e: NumberFormatException) {
            byteArrayOf()
        }

        assertContentEquals(r1, r2)
    }
}