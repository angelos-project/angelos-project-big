import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Crash_bd8eea1ebd2b4a516a6e189f348e4d8db9cae155 {
    static final String base64Bytes = String.join("", "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACdXIAAltCrPMX+AYIVOACAAB4cAAAABAvLy8vLy8vLy8vLy8vLy8vdXEAfgACAAAACy8vLy8vLy8vLy+veA==");

    public static void main(String[] args) throws Throwable {
        Crash_bd8eea1ebd2b4a516a6e189f348e4d8db9cae155.class.getClassLoader().setDefaultAssertionStatus(true);
        try {
            Method fuzzerInitialize = org.angproj.big.FuzzerDivisionSelfKt.class.getMethod("fuzzerInitialize");
            fuzzerInitialize.invoke(null);
        } catch (NoSuchMethodException ignored) {
            try {
                Method fuzzerInitialize = org.angproj.big.FuzzerDivisionSelfKt.class.getMethod("fuzzerInitialize", String[].class);
                fuzzerInitialize.invoke(null, (Object) args);
            } catch (NoSuchMethodException ignored1) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.exit(1);
        }
        com.code_intelligence.jazzer.api.CannedFuzzedDataProvider input = new com.code_intelligence.jazzer.api.CannedFuzzedDataProvider(base64Bytes);
        org.angproj.big.FuzzerDivisionSelfKt.fuzzerTestOneInput(input);
    }
}