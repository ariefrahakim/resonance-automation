package tests.api.progression;

import base.BaseApiTest;
import body.progression.UpdateProgressionBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

public class UpdateProgressionTest extends BaseApiTest {

    @Test(priority = 1)
    public void testUpdateProgression() {
        String progressionId = JsonFileManager.readValue(PROGRESSION_ID_FILE, "progressionId");
        String updatedTitle = "Updated Progression " + Utils.generateRandomString(5);

        Response response = authRequest()
                .body(UpdateProgressionBody.build(progressionId, updatedTitle, "Updated description").toString())
                .put("/api/rest/updateProgression");

        Assert.assertEquals(response.getStatusCode(), 200, "Update progression should return 200");
        Assert.assertEquals(response.jsonPath().getString("title"), updatedTitle);
        System.out.println("Progression updated: " + response.jsonPath().getString("title"));
    }
}
