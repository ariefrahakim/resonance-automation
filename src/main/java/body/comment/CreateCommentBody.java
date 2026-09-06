package body.comment;

import org.json.JSONObject;

public class CreateCommentBody {
    public static JSONObject build(String ticketId, String commentBody) {
        JSONObject body = new JSONObject();
        body.put("ticketId", ticketId);
        body.put("body", commentBody);
        body.put("attachment", "");
        body.put("sendEmail", false);
        return body;
    }
}
