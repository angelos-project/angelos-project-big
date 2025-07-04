import com.code_intelligence.jazzer.api.CannedFuzzedDataProvider
import org.angproj.big.FuzzerDivisionKt
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * This file is generated by the Jazzer exporter.
 * It contains the base64-encoded bytes of the fuzzer's serialized state.
 * You can use this file to reproduce the fuzzer's state in a different environment.
 * */
public object DivisionFuzzerExporterKt {
    /**
     * The base64-encoded bytes of the fuzzer's serialized state.
     * This is used to initialize the fuzzer with a specific state.
     * You can replace this with your own serialized state if needed.
     *
     * Fill out new states at the end of the list.
     */
    public val base64Bytes: List<String> = listOf(
        "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACdXIAAltCrPMX+AYIVOACAAB4cAAAABBTU1NTU1NTU1NTU1NTU1NTdXEAfgACAAAADVNTU1NTU1NTU1NTU/94"
    )

    @OptIn(ExperimentalEncodingApi::class, ExperimentalStdlibApi::class)
    @JvmStatic
    public fun main(args: Array<String>) {
        DivisionFuzzerExporterKt::class.java.getClassLoader().setDefaultAssertionStatus(true)
        try {
            val fuzzerInitialize: Method =
                FuzzerDivisionKt::class.java.getMethod("fuzzerInitialize")
            fuzzerInitialize.invoke(null)
        } catch (_: NoSuchMethodException) {
            try {
                val fuzzerInitialize: Method = FuzzerDivisionKt::class.java.getMethod(
                    "fuzzerInitialize",
                    Array<String>::class.java
                )
                fuzzerInitialize.invoke(null, args as Object?)
            } catch (_: NoSuchMethodException) {
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                System.exit(1)
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                System.exit(1)
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            System.exit(1)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            System.exit(1)
        }
        base64Bytes.forEach { base64 ->
            val input = CannedFuzzedDataProvider(base64)
            val f1  = input.consumeBytes(16)
            println("f1: " + f1.toHexString())
            val f2  = input.consumeBytes(16)
            println("f2: " + f2.toHexString())
            println()
        }
    }
}