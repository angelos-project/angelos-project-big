import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

public object ConcatenateCrashFilesKt {
    @JvmStatic
    public fun main(args: Array<String>) {
        try {
            // Define output file
            val outputPath = Paths.get("concatenated_crash_files.txt")
            Files.newBufferedWriter(outputPath).use { writer ->
                // Get all files starting with "Crash_"
                Files.list(Paths.get("."))
                    .filter { path: Path? -> path!!.getFileName().toString().startsWith("Crash_") }
                    .forEach { path: Path? ->
                        try {
                            // Read single line from each file
                            val line = Files.readString(path).trim { it <= ' ' }
                            // Write line followed by comma and newline
                            writer.write(line + ",\n")
                        } catch (e: IOException) {
                            System.err.println("Error reading file " + path + ": " + e.message)
                        }
                    }
            }
            println("Files concatenated successfully into " + outputPath)
        } catch (e: IOException) {
            System.err.println("Error: " + e.message)
        }
    }
}