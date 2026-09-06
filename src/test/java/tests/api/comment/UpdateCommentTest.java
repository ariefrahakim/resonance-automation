package tests.api.comment;

import base.BaseApiTest;
import body.comment.UpdateCommentBody;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonFileManager;
import utils.Utils;

public class UpdateCommentTest extends BaseApiTest {

    @Test(priority = 1)
    public void testUpdateComment() {
        String commentId = JsonFileManager.readValue(COMMENT_ID_FILE, "commentId");
        String updatedBody = "Updated comment " + Utils.generateRandomString(6);

        Response response = authRequest()
                .body(UpdateCommentBody.build(commentId, updatedBody).toString())
                .put("/api/rest/updateComment");

        Assert.assertEquals(response.getStatusCode(), 200, "Update comment should return 200");
        Assert.assertEquals(response.jsonPath().getString("body"), updatedBody);
        System.out.println("Comment updated: " + response.jsonPath().getString("body"));
    }
}
