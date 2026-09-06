package body.progression;

import org.json.JSONObject;
import utils.Utils;

public class CreateProgressionBody {
    public static JSONObject build(String ticketId, String title, String description) {
        JSONObject body = new JSONObject();
        body.put("ticketId", ticketId);
        body.put("title", title);
        body.put("description", description);
        body.put("attachment", "");
        body.put("expectedDone", Utils.getDateTimeAfterDays(3));
        body.put("sendEmail", false);
        return body;
    }
}
