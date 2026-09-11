package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;
import utils.JsonFileManager;

import static io.restassured.RestAssured.given;

/**
 * Base class for all API tests.
 *
 * Sets up RestAssured with the base URL from config.properties and provides
 * two pre-configured request specs:
 *
 *   authRequest()  — includes Authorization: Bearer <token> header
 *                    (token saved by LoginApiTest to token.json)
 *   baseRequest()  — no authentication, used for public endpoints
 *                    and negative auth tests
 *
 * Authentication flow:
 *   1. LoginApiTest.testLoginSuccess() calls POST /api/rest/login
 *   2. The response token is saved to src/main/resources/json/token.json
 *   3. All subsequent @Test methods that call authRequest() read and use
 *      that token as a Bearer token in the Authorization header
 *
 * Shared state files (src/main/resources/json/):
 *   token.json          — Bearer token from login
 *   ticket_id.json      — ticket ID created in CreateTicketTest
 *   comment_id.json     — comment ID created in CreateCommentTest
 *   progression_id.json — progression ID created in CreateProgressionTest
 */
public class BaseApiTest {

    protected static final String TOKEN_FILE       = "src/main/resources/json/token.json";
    protected static final String TICKET_ID_FILE   = "src/main/resources/json/ticket_id.json";
    protected static final String COMMENT_ID_FILE  = "src/main/resources/json/comment_id.json";
    protected static final String PROGRESSION_ID_FILE = "src/main/resources/json/progression_id.json";

    /**
     * Initializes RestAssured base URI before any test in this class runs.
     * Reads the base URL from config.properties so it never needs to be hardcoded.
     */
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.getProperty("baseUrlResonance");
    }

    /**
     * Returns a RestAssured RequestSpecification pre-configured with:
     *   - Content-Type: application/json
     *   - Authorization: Bearer <token>  (token read from token.json)
     *
     * Use this for any endpoint that requires a logged-in user.
     * Throws RuntimeException if token.json is missing or malformed.
     *
     * @return authenticated request specification
     */
    protected RequestSpecification authRequest() {
        String token = JsonFileManager.readValue(TOKEN_FILE, "token");
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token);
    }

    /**
     * Returns a RestAssured RequestSpecification with only Content-Type set.
     * No authentication header is included.
     *
     * Use this for:
     *   - Public endpoints (e.g., activeTickets, login itself)
     *   - Negative tests verifying that unauthenticated requests are rejected
     *
     * @return unauthenticated request specification
     */
    protected RequestSpecification baseRequest() {
        return given().contentType(ContentType.JSON);
    }
}
