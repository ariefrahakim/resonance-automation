package tests.api.progression;

import base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;

public class DeleteProgressionTest extends BaseApiTest {

    @Test(priority = 1)
    public void testDeleteProgression() {
        String progressionId = JsonFileManager.readValue(PROGRESSION_ID_FILE, "progressionId");

        Response response = authRequest()
                .queryParam("id", progressionId)
                .delete("/api/rest/deleteProgression");

        Assert.assertEquals(response.getStatusCode(), 200, "Delete progression should return 200");
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Delete should return success: true");
        System.out.println("Deleted progression: " + progressionId);
    }
}
