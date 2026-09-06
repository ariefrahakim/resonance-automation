package tests.api.comment;

import base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;

public class DeleteCommentTest extends BaseApiTest {

    @Test(priority = 1)
    public void testDeleteComment() {
        String commentId = JsonFileManager.readValue(COMMENT_ID_FILE, "commentId");

        Response response = authRequest()
                .queryParam("id", commentId)
                .delete("/api/rest/deleteComment");

        Assert.assertEquals(response.getStatusCode(), 200, "Delete comment should return 200");
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Delete should return success: true");
        System.out.println("Deleted comment: " + commentId);
    }
}
