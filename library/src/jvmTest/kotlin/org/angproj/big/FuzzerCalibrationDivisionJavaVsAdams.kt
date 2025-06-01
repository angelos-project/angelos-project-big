package org.angproj.big

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import java.math.BigInteger
import kotlin.test.assertEquals
import org.academic.adams.BigInt2 as AdamsBigInt


internal fun adamsOf(data: ByteArray) : AdamsBigInt  {
    return AdamsBigInt.valueOf(BigInteger(data).toString(16), 16)
}


public object FuzzerCalibrationDivisionJavaVsAdams: FuzzerHelper {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider) = withLogic {
        val f1 = data.consumeBytes(64)
        val f2 = data.consumeBytes(64)

        val r1 = try {
            AdamsBigInt.divide(adamsOf(f1), adamsOf(f2)).let {
                Pair(it[0]!!.toString(16), it[1]!!.toString(16)) }
        } catch (e: NumberFormatException) {
            Pair("", "")
        } catch (e: ArithmeticException) {
            Pair("", "")
        } catch (e: BigMathException) {
            Pair("", "")
        }


        val r2 = try {
            BigInteger(f1).divideAndRemainder(BigInteger(f2)).let {
                Pair(it[0].toString(16), it[1].toString(16)) }
        } catch (e: NumberFormatException) {
            Pair("", "")
        } catch (e: ArithmeticException) {
            Pair("", "")
        }

        assertEquals(r1.first, r2.first)
        assertEquals(r1.second, r2.second)
    }
}