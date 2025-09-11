import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Crash_a64de8a5bebdb7fd11d2f824b6bf7f1363773378 {
    static final String base64Bytes = String.join("", "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACdXIAAltCrPMX+AYIVOACAAB4cAAAABAAAAAATPvr6+t2/34AAAAAdXEAfgACAAAACUz76+vrdv9+fng=");

    public static void main(String[] args) throws Throwable {
        Crash_a64de8a5bebdb7fd11d2f824b6bf7f1363773378.class.getClassLoader().setDefaultAssertionStatus(true);
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