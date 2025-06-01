package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertContentEquals


public object FuzzerSetBit: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeBytes(64)
        val f2 = data.consumeByte().toInt()

        val r1 = try {
            bigIntOf(f1).setBit(f2).toByteArray()
        } catch (e: BigMathException) {
            byteArrayOf()
        }

        val r2 = try {
            BigInteger(f1).setBit(f2).toByteArray()
        } catch (e: NumberFormatException) {
            byteArrayOf()
        } catch (e: ArithmeticException) {
            byteArrayOf()
        }

        assertContentEquals(r1, r2)
    }
}