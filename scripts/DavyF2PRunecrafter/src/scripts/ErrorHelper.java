package scripts;

public class ErrorHelper {

    public static void throwError(String errorMessage) {
        throw new RuntimeException(errorMessage);
    }

}
