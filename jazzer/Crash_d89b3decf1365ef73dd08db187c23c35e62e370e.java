import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Crash_d89b3decf1365ef73dd08db187c23c35e62e370e {
    static final String base64Bytes = String.join("", "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACdXIAAltCrPMX+AYIVOACAAB4cAAAABCj7+/v7+/v7+/v7+/v7+/vdXEAfgACAAAADO/v7+/v7+/v7+/vAHg=");

    public static void main(String[] args) throws Throwable {
        Crash_d89b3decf1365ef73dd08db187c23c35e62e370e.class.getClassLoader().setDefaultAssertionStatus(true);
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