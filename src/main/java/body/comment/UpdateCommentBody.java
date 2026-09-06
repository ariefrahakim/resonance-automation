package body.comment;

import org.json.JSONObject;

public class UpdateCommentBody {
    public static JSONObject build(String id, String commentBody) {
        JSONObject body = new JSONObject();
        body.put("id", id);
        body.put("body", commentBody);
        body.put("attachment", "");
        return body;
    }
}
