package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;
import utils.JsonFileManager;

import static io.restassured.RestAssured.given;

public class BaseApiTest {
    protected static final String TOKEN_FILE = "src/main/resources/json/token.json";
    protected static final String TICKET_ID_FILE = "src/main/resources/json/ticket_id.json";
    protected static final String COMMENT_ID_FILE = "src/main/resources/json/comment_id.json";
    protected static final String PROGRESSION_ID_FILE = "src/main/resources/json/progression_id.json";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrlResonance");
    }

    protected RequestSpecification authRequest() {
        String token = JsonFileManager.readValue(TOKEN_FILE, "token");
        // Resonance menggunakan Next-Auth: token dikirim via cookie, bukan Bearer header
        return given()
                .contentType(ContentType.JSON)
                .cookie("__Secure-next-auth.session-token", token);
    }

    protected RequestSpecification baseRequest() {
        return given().contentType(ContentType.JSON);
    }
}
