package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertContentEquals


public object FuzzerMod: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeBytes(64)
        val f2 = data.consumeBytes(64)

        val r1 = try {
            bigIntOf(f1).mod(bigIntOf(f2)).toByteArray()
        } catch (e: BigMathException) {
            byteArrayOf()
        }

        val r2 = try {
            BigInteger(f1).mod(BigInteger(f2)).toByteArray()
        } catch (e: NumberFormatException) {
            byteArrayOf()
        } catch (e: ArithmeticException) {
            byteArrayOf()
        }

        assertContentEquals(r1, r2)
    }
}