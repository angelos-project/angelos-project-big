package org.angproj.big

import kotlin.test.*
import java.math.BigInteger as JavaBigInteger

class ExperimentDebug {
    // Should be fixed
    /**
     * This test recognizes that BigInt can predict its export size properly.
     * */
    //@Test
    fun testToSize_asReference(): Unit = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                getByteSize(xBi2),
                xBi2.toByteArray().size,
            )
        }
    }

    // Should be fixed
    /**
     * This test recognizes that BigInt can predict its export size properly.
     * */
    //@Test
    fun testToSize_forceError(): Unit = withLogic {
        //Combinator.numberGenerator(-64..64) { x ->
            val x = byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte())
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)


            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                getByteSize(xBi2),
                xBi2.toByteArray().size,
            )
        //}
    }
}