import com.code_intelligence.jazzer.Jazzer
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Jazzer fuzzer implementation custom for this project, mainly to be
 * run from the IntelliJ Idea Community Edition or better if you like.
 *
 * 1. The first step is to download the project from github and set it
 * up as a project in this
 *
 * 4. When you are done building the project, go to the folder ./build/classes/kotlin/jvm/test/org/angproj/big/
 * and see if you recognize the <Fuzzer*> class files, make a choice of your liking
 * and change the fuzzerImplClz string to hold the name of your chosen fuzzing class.
 *
 * Finally, either right-click on this file to the left in the Project view and click
 * Run 'JazzerImplKt', or click run 'JazzerImplKt' from the upper right build/run button
 *
 * The fuzzer should now run for a long term trying all type of fuzzing tests and in
 * worst case come with a crash report in the root folder of this project.
 *
 * It's necessary to report back which tests you ran and on what computer platform and
 * also return the output logs as evidence and if crashes even more important to file the
 * crash report, or if you like, help is recover from such a failure and earn true reputation
 * among us.
 * */
fun main(args: Array<String>) {

    val fuzzerAllCases: List<String> = listOf(
        "Abs", // Just fix
        "Addition", // Just fix
        "And", // Just fix
        "AndNot", // Just fix
        "Bytes", // Fixed, No 4
        "ClearBit", // Just fix
        "Compare", // Just fix
        "Division", // 7 <---- Arbitrary failure repeatedly
        "FlipBit", // Just fix
        "Long", // Just fix
        "Max", // Just fix
        "Min", // Just fix
        "Mod", // 12 <---- Fails because of "Division", it is dependant // Presumably fixed by new loading
        "Multiplication", // Fixed
        "Negate", // Just fix
        "Not", // Just fix
        "Or", // Just fix
        "Pow",
        "SetBit", // Just fix, No 18
        "ShiftLeft", // Fixed
        "ShiftRight", // Fixed
        "Subtraction", // Just fix
        "TestBit", // Just fix
        "Xor" // Just fix, 23
    )

    val fuzzerImplClz: String = "Fuzzer" + fuzzerAllCases[13] // <---- Set any fuzzing case available
    //val fuzzerImplClz: String = "FuzzerCalibrationValueOfJavaVsNewBig"


    val fuzzerTimeSecs: Int = 2.toDuration(
        DurationUnit.MINUTES
    ).toInt(DurationUnit.SECONDS) // <---- Max time of fuzzing before shut down if no crash reported

    Jazzer.main(arrayOf(
        "--target_class=org.angproj.big.$fuzzerImplClz",
        "-max_total_time=$fuzzerTimeSecs"
    ))
}
