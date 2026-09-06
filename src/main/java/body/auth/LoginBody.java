package body.auth;

import org.json.JSONObject;

public class LoginBody {
    public static JSONObject build(String usernameOrEmail, String password) {
        JSONObject body = new JSONObject();
        body.put("usernameOrEmail", usernameOrEmail);
        body.put("password", password);
        return body;
    }
}
