package scripts;

import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.Login;

import static scripts.ErrorHelper.throwError;

public class LoginHelper {

    public static boolean attemptLogin() {
        if (!Login.isLoggedIn()) {
            Log.info("Not logged in, attempting to log in...");
            if (!Login.login()) {
                Log.error("Login failed, check your credentials and client settings.");
                throwError("Login failed, check your credentials and client settings.");
                return false;
            }
        }
        return true;
    }
}
