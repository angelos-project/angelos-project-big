import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

public object ConcatenateCrashFiles {
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
                            // Read all lines from the file
                            val lines: Array<String?> =
                                Files.readString(path).split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            // Check if file has at least 5 lines
                            if (lines.size >= 5) {
                                // Get the 5th line (index 4) and trim commas
                                val line = lines[4]!!.replace("^,+|,+$".toRegex(), "")
                                // Write line followed by comma and newline
                                writer.write(line + ",\n")
                            }
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