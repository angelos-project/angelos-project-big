package org.angproj.big.newbig

import com.code_intelligence.jazzer.Jazzer
import com.code_intelligence.jazzer.api.FuzzedDataProvider
import org.angproj.big.BigMathException
import org.angproj.big.withLogic
import java.math.BigInteger
import kotlin.test.assertContentEquals
import kotlin.time.Duration.Companion.minutes


public object FuzzerDivisionKt {
    @JvmStatic
    public fun fuzzerTestOneInput(data: FuzzedDataProvider): Unit = withLogic {
        val f1 = data.consumeBytes(16)
        val f2 = data.consumeBytes(16)

        val r1 = try {
            internalOf(f1).divideAndRemainder(internalOf(f2)).let {
                Pair(
                    toByteArray(it.first.mag, it.first.sigNum),
                    toByteArray(it.second.mag, it.second.sigNum)
                )
            }
        } catch (e: BigMathException) {
            Pair(byteArrayOf(), byteArrayOf())
        }

        val r2 = try {
            BigInteger(f1).divideAndRemainder(BigInteger(f2)).let {
                Pair(it[0].toByteArray(), it[1].toByteArray())
            }
        } catch (e: NumberFormatException) {
            Pair(byteArrayOf(), byteArrayOf())
        } catch (e: ArithmeticException) {
            Pair(byteArrayOf(), byteArrayOf())
        }

        //assertEquals(r1.first, r2.first)
        //assertEquals(r1.second, r2.second)
        assertContentEquals(r1.first, r2.first)
        assertContentEquals(r1.second, r2.second)
    }

    @JvmStatic
    public fun main(args: Array<String>) {
        Jazzer.main(arrayOf(
            "--target_class=${javaClass.name}",
            "-max_total_time=${2.minutes.inWholeSeconds}"
        ))
    }
}