import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Crash_34eb2f42d6b2c794ba56fd78a74286cff09adea4 {
    static final String base64Bytes = String.join("", "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACdXIAAltCrPMX+AYIVOACAAB4cAAAABAAAAAAvwoAZmZmZmb///9DdXEAfgACAAAACWZmZmZmZmZm/3g=");

    public static void main(String[] args) throws Throwable {
        Crash_34eb2f42d6b2c794ba56fd78a74286cff09adea4.class.getClassLoader().setDefaultAssertionStatus(true);
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