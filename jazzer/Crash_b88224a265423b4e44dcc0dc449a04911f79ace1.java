import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Crash_b88224a265423b4e44dcc0dc449a04911f79ace1 {
    static final String base64Bytes = String.join("", "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACdXIAAltCrPMX+AYIVOACAAB4cAAAABAK+/v7+/v7+/v7+/v7+/v7dXEAfgACAAAAC/v7+/v7+/v7+/v7eA==");

    public static void main(String[] args) throws Throwable {
        Crash_b88224a265423b4e44dcc0dc449a04911f79ace1.class.getClassLoader().setDefaultAssertionStatus(true);
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