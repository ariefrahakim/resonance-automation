package body.vote;

import org.json.JSONObject;

public class VoteBody {
    public static JSONObject build(String ticketId) {
        JSONObject body = new JSONObject();
        body.put("ticketId", ticketId);
        return body;
    }
}
