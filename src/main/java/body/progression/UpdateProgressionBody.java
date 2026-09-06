package body.progression;

import org.json.JSONObject;
import utils.Utils;

public class UpdateProgressionBody {
    public static JSONObject build(String id, String title, String description) {
        JSONObject body = new JSONObject();
        body.put("id", id);
        body.put("title", title);
        body.put("description", description);
        body.put("attachment", "");
        body.put("expectedDone", Utils.getDateTimeAfterDays(5));
        return body;
    }
}
