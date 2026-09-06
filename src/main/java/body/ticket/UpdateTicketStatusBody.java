package body.ticket;

import org.json.JSONObject;

public class UpdateTicketStatusBody {
    public static JSONObject buildSolved(String ticketId, String solvedAt) {
        JSONObject body = new JSONObject();
        body.put("ticketId", ticketId);
        body.put("solvedAt", solvedAt);
        body.put("sendEmail", false);
        return body;
    }

    public static JSONObject buildUnsolved(String ticketId) {
        JSONObject body = new JSONObject();
        body.put("ticketId", ticketId);
        body.put("solvedAt", JSONObject.NULL);
        body.put("sendEmail", false);
        return body;
    }
}
