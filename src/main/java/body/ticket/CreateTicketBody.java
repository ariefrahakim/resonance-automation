package body.ticket;

import org.json.JSONObject;

public class CreateTicketBody {
    public static JSONObject build(String title, String description, boolean isPublic) {
        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("description", description);
        body.put("attachment", JSONObject.NULL);
        body.put("isPublic", isPublic);
        return body;
    }
}
