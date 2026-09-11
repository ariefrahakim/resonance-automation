#!/usr/bin/env python3
"""
Creates Day33_Data_Binding_UI_API_Automation.pptx
Based on the template style from A07_Selenium_Java_Automation_Framework_1.pptx

Topic: Day-33 — Data Binding in UI & API Automation
Exercise: Using data binding for UI and API automation with Selenium and REST Assured
"""

from pptx import Presentation
from pptx.util import Inches, Pt, Emu
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.oxml.ns import qn
from pptx.util import Pt
import copy
from lxml import etree

TEMPLATE = '/Users/ariefrahman/Desktop/resonance-automation/docs/A07_Selenium_Java_Automation_Framework_1.pptx'
OUTPUT   = '/Users/ariefrahman/Desktop/resonance-automation/docs/Day33_Data_Binding_UI_API_Automation.pptx'

# ===== Color palette (matching Resonance framework theme) =====
DARK_BLUE   = RGBColor(0x1F, 0x49, 0x7D)   # dark blue header
ACCENT_BLUE = RGBColor(0x2E, 0x75, 0xB6)   # accent blue
LIGHT_BLUE  = RGBColor(0xBD, 0xD7, 0xEE)   # light blue fill
GREEN       = RGBColor(0x00, 0x70, 0x00)   # positive/pass green
WHITE       = RGBColor(0xFF, 0xFF, 0xFF)
DARK_GRAY   = RGBColor(0x26, 0x26, 0x26)
CODE_BG     = RGBColor(0xF2, 0xF2, 0xF2)   # light gray for code blocks
CODE_FG     = RGBColor(0x00, 0x33, 0x66)   # code text color

prs = Presentation(TEMPLATE)
layout = prs.slide_layouts[0]

# Remove all existing slides
xml_slides = prs.slides._sldIdLst
while len(xml_slides) > 0:
    xml_slides.remove(xml_slides[0])

W = prs.slide_width
H = prs.slide_height


def add_slide():
    """Add a blank slide using the only available layout."""
    slide = prs.slides.add_slide(layout)
    # Remove all placeholder shapes (start clean)
    for shape in list(slide.shapes):
        sp = shape._element
        sp.getparent().remove(sp)
    return slide


def add_rect(slide, left, top, width, height, fill_color, line_color=None):
    """Add a colored rectangle shape."""
    shape = slide.shapes.add_shape(
        1,  # MSO_SHAPE_TYPE.RECTANGLE
        Inches(left), Inches(top), Inches(width), Inches(height)
    )
    fill = shape.fill
    fill.solid()
    fill.fore_color.rgb = fill_color
    line = shape.line
    if line_color:
        line.color.rgb = line_color
        line.width = Pt(1)
    else:
        line.fill.background()
    return shape


def add_text(slide, text, left, top, width, height,
             font_size=18, bold=False, color=DARK_GRAY,
             align=PP_ALIGN.LEFT, italic=False, font_name='Calibri'):
    """Add a text box."""
    txBox = slide.shapes.add_textbox(
        Inches(left), Inches(top), Inches(width), Inches(height)
    )
    tf = txBox.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.alignment = align
    run = p.add_run()
    run.text = text
    run.font.size = Pt(font_size)
    run.font.bold = bold
    run.font.italic = italic
    run.font.color.rgb = color
    run.font.name = font_name
    return txBox


def add_bullet_list(slide, items, left, top, width, height,
                    font_size=16, color=DARK_GRAY, indent=0.3):
    """Add a text box with bulleted items."""
    txBox = slide.shapes.add_textbox(
        Inches(left), Inches(top), Inches(width), Inches(height)
    )
    tf = txBox.text_frame
    tf.word_wrap = True
    first = True
    for item in items:
        if first:
            p = tf.paragraphs[0]
            first = False
        else:
            p = tf.add_paragraph()
        p.space_before = Pt(4)
        run = p.add_run()
        run.text = item
        run.font.size = Pt(font_size)
        run.font.color.rgb = color
        run.font.name = 'Calibri'
    return txBox


def slide_header(slide, title, subtitle=None):
    """Standard slide header: dark blue bar at top."""
    add_rect(slide, 0, 0, 10, 0.7, DARK_BLUE)
    add_text(slide, title, 0.2, 0.05, 9.6, 0.6,
             font_size=22, bold=True, color=WHITE, align=PP_ALIGN.LEFT)
    if subtitle:
        add_text(slide, subtitle, 0.2, 0.65, 9.6, 0.35,
                 font_size=13, color=ACCENT_BLUE, italic=True)


# =========================================================
# SLIDE 1 — Title Slide
# =========================================================
slide = add_slide()
add_rect(slide, 0, 0, 10, 5.6, DARK_BLUE)
add_rect(slide, 0, 2.5, 10, 0.08, ACCENT_BLUE)
add_text(slide, 'Day-33', 0.5, 0.5, 9, 1,
         font_size=28, bold=True, color=LIGHT_BLUE, align=PP_ALIGN.CENTER)
add_text(slide, 'Data Binding in UI & API Automation', 0.5, 1.3, 9, 1.2,
         font_size=36, bold=True, color=WHITE, align=PP_ALIGN.CENTER)
add_text(slide, 'Using Selenium + REST Assured with Java',
         0.5, 2.6, 9, 0.7, font_size=20, color=LIGHT_BLUE, align=PP_ALIGN.CENTER)
add_text(slide, 'Resonance Automation Framework  |  QA Engineering',
         0.5, 3.4, 9, 0.5, font_size=14, color=LIGHT_BLUE, align=PP_ALIGN.CENTER, italic=True)
add_text(slide, 'Exercise: Using data binding for UI and API automation\nwith Selenium and REST Assured',
         0.5, 4.3, 9, 0.9, font_size=13, color=ACCENT_BLUE, align=PP_ALIGN.CENTER, italic=True)

# =========================================================
# SLIDE 2 — Agenda
# =========================================================
slide = add_slide()
slide_header(slide, 'Agenda', 'What we will cover in this session')
topics = [
    '01.  What is Data Binding / Data-Driven Testing?',
    '02.  Data Binding in API Automation — TestNG @DataProvider',
    '03.  Data Binding in UI Automation — Cucumber Scenario Outline',
    '04.  Architecture: How data flows through the framework',
    '05.  State Sharing with JSON files (token, ticket ID, etc.)',
    '06.  Bearer Token Authentication Flow',
    '07.  Live Demo & Hands-on Exercise',
    '08.  Best Practices & Summary',
]
add_bullet_list(slide, topics, 0.3, 0.9, 9.4, 4.5, font_size=17)

# =========================================================
# SLIDE 3 — What is Data-Driven Testing?
# =========================================================
slide = add_slide()
slide_header(slide, 'What is Data-Driven Testing?', 'Separating test logic from test data')

add_text(slide, 'Data-Driven Testing (DDT) runs the same test logic\nwith different input values — automatically.',
         0.3, 0.9, 9.4, 0.8, font_size=17, color=DARK_BLUE, bold=True)

cols = [
    ('WITHOUT Data Binding', [
        '• One test method per scenario',
        '• Duplicate code for each case',
        '• Hard to maintain or extend',
        '• Hardcoded values in test code',
    ], 0.3, 1.9, 4.4),
    ('WITH Data Binding', [
        '• One test method, many data rows',
        '• Logic written once, runs N times',
        '• Add new cases by adding a row',
        '• Data sourced from config/file/method',
    ], 5.2, 1.9, 4.4),
]
for title, bullets, x, y, w in cols:
    color = RGBColor(0xC0, 0x00, 0x00) if 'WITHOUT' in title else GREEN
    add_rect(slide, x, y, w, 0.45, color if 'WITHOUT' in title else DARK_BLUE)
    add_text(slide, title, x + 0.1, y + 0.05, w - 0.2, 0.35,
             font_size=15, bold=True, color=WHITE)
    add_bullet_list(slide, bullets, x + 0.1, y + 0.5, w - 0.1, 2.5, font_size=14)

add_rect(slide, 0.3, 4.5, 9.4, 0.65, LIGHT_BLUE)
add_text(slide, '→  In this repo: TestNG @DataProvider (API) + Cucumber Scenario Outline (Web)',
         0.5, 4.55, 9, 0.5, font_size=15, color=DARK_BLUE, bold=True)

# =========================================================
# SLIDE 4 — @DataProvider in API Tests
# =========================================================
slide = add_slide()
slide_header(slide, 'Data Binding — API: TestNG @DataProvider',
             'src/test/java/data/  →  DataProvider classes supply rows to @Test methods')

add_text(slide, 'How it works:', 0.3, 0.85, 9.4, 0.4, font_size=15, bold=True, color=DARK_BLUE)
steps = [
    '1.  @DataProvider method returns Object[][] — outer array = rows, inner = columns',
    '2.  @Test method declares matching parameters (position-matched)',
    '3.  TestNG calls the @Test method once per row — parallel or sequential',
    '4.  Credentials & config read from ConfigReader (never hardcoded!)',
]
add_bullet_list(slide, steps, 0.5, 1.25, 9, 1.5, font_size=14)

add_rect(slide, 0.3, 2.7, 9.4, 0.35, DARK_BLUE)
add_text(slide, 'LoginDataProvider.java  (data/LoginDataProvider.java)',
         0.5, 2.72, 9, 0.3, font_size=13, bold=True, color=WHITE)

code1 = (
    '@DataProvider(name = "loginValidData")\n'
    'public static Object[][] loginValidData() {\n'
    '    String user = ConfigReader.getProperty("usernameOrEmailResonance");\n'
    '    String pass = ConfigReader.getProperty("passwordResonance");\n'
    '    return new Object[][] {\n'
    '        { user, pass, "Login with valid username from config" },\n'
    '    };\n'
    '}'
)
add_rect(slide, 0.3, 3.05, 9.4, 1.9, CODE_BG)
add_text(slide, code1, 0.5, 3.1, 9, 1.8,
         font_size=11, color=CODE_FG, font_name='Courier New')

# =========================================================
# SLIDE 5 — @Test consuming @DataProvider
# =========================================================
slide = add_slide()
slide_header(slide, '@Test Method Consuming @DataProvider',
             'src/test/java/tests/api/auth/LoginApiTest.java')

add_text(slide, 'The @Test method receives one row per execution:', 0.3, 0.85, 9.4, 0.4,
         font_size=15, bold=True, color=DARK_BLUE)

code2 = (
    '@Test(priority = 2,\n'
    '      dataProvider = "loginValidData",\n'
    '      dataProviderClass = LoginDataProvider.class,\n'
    '      description = "Login with multiple valid credential sets")\n'
    'public void testLoginWithValidData(\n'
    '        String username, String password, String scenario) {\n'
    '\n'
    '    System.out.println("[INFO] Scenario: " + scenario);\n'
    '\n'
    '    Response response = baseRequest()\n'
    '            .body(LoginBody.build(username, password).toString())\n'
    '            .post("/api/rest/login");\n'
    '\n'
    '    Assert.assertEquals(response.getStatusCode(), 200,\n'
    '            "Login must succeed for: " + scenario);\n'
    '    Assert.assertNotNull(response.jsonPath().getString("token"),\n'
    '            "Bearer token must be returned");\n'
    '}'
)
add_rect(slide, 0.3, 1.25, 9.4, 3.9, CODE_BG)
add_text(slide, code2, 0.5, 1.3, 9, 3.8,
         font_size=10.5, color=CODE_FG, font_name='Courier New')

add_rect(slide, 0.3, 5.1, 9.4, 0.35, LIGHT_BLUE)
add_text(slide, '→  TestNG runs this method once per row in loginValidData — same code, different inputs',
         0.5, 5.13, 9, 0.3, font_size=13, color=DARK_BLUE)

# =========================================================
# SLIDE 6 — Scenario Outline in Web Tests
# =========================================================
slide = add_slide()
slide_header(slide, 'Data Binding — Web: Cucumber Scenario Outline',
             'src/test/resources/features/login.feature')

add_text(slide, 'Scenario Outline + Examples table = data binding in Gherkin BDD:',
         0.3, 0.85, 9.4, 0.4, font_size=15, bold=True, color=DARK_BLUE)

code3 = (
    '@negative\n'
    'Scenario Outline: Login fails with invalid credentials\n'
    '  When  I enter username "<username>"\n'
    '  And   I enter password "<password>"\n'
    '  And   I click the Login button\n'
    '  Then  I should see an error or stay on the login page\n'
    '\n'
    '  Examples:\n'
    '    | username                  | password      |\n'
    '    | unknown_user@fake.com     | password      |\n'
    '    | user1                     | wrongpassword |\n'
    '    | user1                     | ab            |'
)
add_rect(slide, 0.3, 1.3, 9.4, 3.0, CODE_BG)
add_text(slide, code3, 0.5, 1.35, 9, 2.9,
         font_size=11, color=CODE_FG, font_name='Courier New')

points = [
    '→  Cucumber runs the scenario once per row in the Examples table',
    '→  <placeholder> values are substituted automatically at runtime',
    '→  Step definitions stay the same — data changes, logic does not',
]
add_bullet_list(slide, points, 0.3, 4.4, 9.4, 1.0, font_size=14, color=DARK_BLUE)

# =========================================================
# SLIDE 7 — Framework Architecture
# =========================================================
slide = add_slide()
slide_header(slide, 'Framework Architecture — Data Flow',
             'How test data moves from source to assertion')

boxes = [
    (0.3,  1.1, 2.8, 'config.properties\n+ DataProvider\n+ Examples table',   ACCENT_BLUE),
    (3.6,  1.1, 2.8, 'Test Method / Step\nDefinition\n(@Test / @When)',         DARK_BLUE),
    (6.9,  1.1, 2.8, 'Assertion\nAssert.assertEquals\n/ Then clause',           GREEN),
    (0.3,  2.9, 2.8, 'Page Object\n(pages/LoginPage.java)',                      ACCENT_BLUE),
    (3.6,  2.9, 2.8, 'API Request\n(authRequest() /\nbaseRequest())',            DARK_BLUE),
    (6.9,  2.9, 2.8, 'JSON State Files\n(token.json,\nticket_id.json)',          RGBColor(0x70, 0x30, 0xA0)),
]
for x, y, w, label, color in boxes:
    add_rect(slide, x, y, w, 0.9, color)
    add_text(slide, label, x + 0.1, y + 0.05, w - 0.2, 0.8,
             font_size=12, bold=True, color=WHITE, align=PP_ALIGN.CENTER)

# Arrows (simple text arrows)
add_text(slide, '→', 3.15, 1.45, 0.4, 0.3, font_size=20, bold=True, color=DARK_GRAY)
add_text(slide, '→', 6.45, 1.45, 0.4, 0.3, font_size=20, bold=True, color=DARK_GRAY)
add_text(slide, '→', 3.15, 3.25, 0.4, 0.3, font_size=20, bold=True, color=DARK_GRAY)
add_text(slide, '→', 6.45, 3.25, 0.4, 0.3, font_size=20, bold=True, color=DARK_GRAY)

add_rect(slide, 0.3, 4.3, 9.4, 0.95, LIGHT_BLUE)
flow = 'API Flow:  DataProvider → BaseApiTest.authRequest() → REST Assured → Response Assertion\nWeb Flow:  Examples Table → Step Definition → Page Object → Selenium Action → Cucumber Then'
add_text(slide, flow, 0.5, 4.35, 9, 0.85, font_size=13, color=DARK_BLUE)

# =========================================================
# SLIDE 8 — State Sharing with JSON Files
# =========================================================
slide = add_slide()
slide_header(slide, 'State Sharing Between Test Classes',
             'src/main/resources/json/ — Bridges test execution order')

add_text(slide, 'Why JSON state files?', 0.3, 0.85, 9.4, 0.4,
         font_size=15, bold=True, color=DARK_BLUE)
add_text(slide,
         'TestNG tests run in priority order across classes. One class creates a resource (ticket),\n'
         'and a subsequent class needs its ID. JSON files bridge that gap without a database.',
         0.3, 1.2, 9.4, 0.7, font_size=14, color=DARK_GRAY)

code4 = (
    '// LoginApiTest — saves token after successful login\n'
    'String token = response.jsonPath().getString("token");\n'
    'JsonFileManager.writeValue(TOKEN_FILE, "token", token);\n'
    '\n'
    '// BaseApiTest — reads token for every authenticated request\n'
    'protected RequestSpecification authRequest() {\n'
    '    String token = JsonFileManager.readValue(TOKEN_FILE, "token");\n'
    '    return given()\n'
    '            .contentType(ContentType.JSON)\n'
    '            .header("Authorization", "Bearer " + token);\n'
    '}'
)
add_rect(slide, 0.3, 1.9, 9.4, 2.4, CODE_BG)
add_text(slide, code4, 0.5, 1.95, 9, 2.3,
         font_size=11, color=CODE_FG, font_name='Courier New')

files = [
    ('token.json',           '{ "token": "eyJhbGci..." }',              'Bearer token from POST /api/rest/login'),
    ('ticket_id.json',       '{ "ticketId": "cmtx5pkz..." }',           'Ticket ID from POST /api/rest/createTicket'),
    ('comment_id.json',      '{ "commentId": "cm7abc..." }',            'Comment ID from POST /api/rest/createComment'),
    ('progression_id.json',  '{ "progressionId": "cm9xyz..." }',        'Progression ID from POST /api/rest/createProgression'),
]
y = 4.4
for fname, example, desc in files:
    add_rect(slide, 0.3, y, 1.8, 0.25, DARK_BLUE)
    add_text(slide, fname, 0.35, y + 0.02, 1.7, 0.22,
             font_size=10, bold=True, color=WHITE, font_name='Courier New')
    add_text(slide, example, 2.2, y + 0.02, 3.5, 0.22,
             font_size=10, color=CODE_FG, font_name='Courier New')
    add_text(slide, desc, 5.8, y + 0.02, 4, 0.22, font_size=10, color=DARK_GRAY)
    y += 0.28

# =========================================================
# SLIDE 9 — Bearer Token Authentication Flow
# =========================================================
slide = add_slide()
slide_header(slide, 'Bearer Token Authentication Flow',
             'POST /api/rest/login → token → Authorization: Bearer <token>')

steps_auth = [
    ('Step 1', 'LoginApiTest runs first (priority = 1)',
     'POST /api/rest/login with credentials from config.properties'),
    ('Step 2', 'Server validates credentials, returns Bearer token',
     'Response: { "ok": true, "token": "eyJhbGci...", "user": { ... } }'),
    ('Step 3', 'Token saved to token.json via JsonFileManager.writeValue()',
     'File: src/main/resources/json/token.json'),
    ('Step 4', 'All subsequent tests call authRequest() which reads token.json',
     'Header added: Authorization: Bearer <token>'),
    ('Step 5', 'Server validates the Bearer token on protected endpoints',
     'If token is missing/invalid → 401 Unauthorized'),
]
y = 0.95
for label, title, detail in steps_auth:
    add_rect(slide, 0.3, y, 1.2, 0.7, DARK_BLUE)
    add_text(slide, label, 0.35, y + 0.2, 1.1, 0.3,
             font_size=12, bold=True, color=WHITE, align=PP_ALIGN.CENTER)
    add_rect(slide, 1.6, y, 8.1, 0.7, LIGHT_BLUE)
    add_text(slide, title, 1.7, y + 0.03, 7.9, 0.35, font_size=13, bold=True, color=DARK_BLUE)
    add_text(slide, detail, 1.7, y + 0.38, 7.9, 0.28, font_size=11, color=DARK_GRAY)
    y += 0.82

# =========================================================
# SLIDE 10 — Live Demo / Exercise Setup
# =========================================================
slide = add_slide()
slide_header(slide, 'Hands-on Exercise Setup',
             'Clone → Configure → Run → Observe')

add_text(slide, '1.  Clone the Repository', 0.3, 0.9, 9.4, 0.4,
         font_size=16, bold=True, color=DARK_BLUE)
add_rect(slide, 0.3, 1.3, 9.4, 0.5, CODE_BG)
add_text(slide, 'git clone https://github.com/ariefrahakim/resonance-automation.git\ncd resonance-automation',
         0.5, 1.32, 9, 0.45, font_size=11, color=CODE_FG, font_name='Courier New')

add_text(slide, '2.  Review Configuration', 0.3, 1.9, 9.4, 0.35,
         font_size=16, bold=True, color=DARK_BLUE)
add_rect(slide, 0.3, 2.25, 9.4, 0.5, CODE_BG)
add_text(slide, '# src/main/resources/config.properties\nbaseUrlResonance=https://resonance.dibimbing.id\nusernameOrEmailResonance=user1\npasswordResonance=password',
         0.5, 2.27, 9, 0.45, font_size=11, color=CODE_FG, font_name='Courier New')

add_text(slide, '3.  Run Tests', 0.3, 2.85, 9.4, 0.35,
         font_size=16, bold=True, color=DARK_BLUE)
add_rect(slide, 0.3, 3.2, 9.4, 0.7, CODE_BG)
add_text(slide, './gradlew clean apiTest      # Run API tests (TestNG + REST Assured)\n./gradlew clean webTest      # Run Web tests (Cucumber + Selenium)\n./gradlew allureServe        # Open Allure HTML report in browser',
         0.5, 3.22, 9, 0.65, font_size=11, color=CODE_FG, font_name='Courier New')

add_text(slide, '4.  Observe Data Binding in Action', 0.3, 3.95, 9.4, 0.35,
         font_size=16, bold=True, color=DARK_BLUE)
obs = [
    '→  API: LoginApiTest runs testLoginWithValidData() once per row in loginValidData',
    '→  API: CreateTicketTest runs 3 times (public, private, bug report)',
    '→  Web: Login scenario outline runs 3 times with different credential rows',
]
add_bullet_list(slide, obs, 0.5, 4.35, 9, 0.9, font_size=13, color=DARK_GRAY)

# =========================================================
# SLIDE 11 — Exercise Task
# =========================================================
slide = add_slide()
slide_header(slide, 'Exercise — Your Task',
             'Apply data binding to UI and API automation using Selenium and REST Assured')

add_rect(slide, 0.3, 0.85, 9.4, 0.45, DARK_BLUE)
add_text(slide, 'Exercise: Data Binding for UI & API Automation',
         0.5, 0.88, 9, 0.38, font_size=17, bold=True, color=WHITE)

tasks_api = [
    'Task 1 (API): Add a new row to CommentDataProvider with your own comment text',
    '             Run CreateCommentTest — verify it runs for all rows including yours',
    'Task 2 (API): Add a negative row to createTicketInvalidData (e.g., title > 200 chars)',
    '             Run NegativeTicketTest — verify the new case is rejected by the API',
    'Task 3 (API): Modify LoginDataProvider.loginInvalidData to use Utils.generateRandomEmail()',
    '             Verify each run uses a different random email (check console output)',
]
tasks_web = [
    'Task 4 (Web): Add a new row to the login.feature Scenario Outline Examples table',
    '             Run webTest — verify your new row is executed as a separate scenario',
    'Task 5 (Web): Create a new Scenario Outline for Create Ticket with 2+ data rows',
    '             Add it to create_ticket.feature and run the web suite',
]

add_text(slide, 'API Tasks (REST Assured + TestNG @DataProvider):', 0.3, 1.4, 9.4, 0.35,
         font_size=14, bold=True, color=ACCENT_BLUE)
add_bullet_list(slide, tasks_api, 0.5, 1.75, 9, 1.7, font_size=12)

add_text(slide, 'Web Tasks (Selenium + Cucumber Scenario Outline):', 0.3, 3.5, 9.4, 0.35,
         font_size=14, bold=True, color=ACCENT_BLUE)
add_bullet_list(slide, tasks_web, 0.5, 3.85, 9, 1.3, font_size=12)

add_rect(slide, 0.3, 5.15, 9.4, 0.35, GREEN)
add_text(slide, 'Goal: understand that test logic stays the same — only data changes.',
         0.5, 5.18, 9, 0.3, font_size=13, bold=True, color=WHITE)

# =========================================================
# SLIDE 12 — Best Practices
# =========================================================
slide = add_slide()
slide_header(slide, 'Best Practices — Data Binding in QA Automation', '')

practices = [
    ('Never hardcode credentials',
     'Use ConfigReader to read from config.properties. One change propagates everywhere.'),
    ('Use Utils for randomness',
     'Utils.generateRandomString() / generateRandomEmail() ensure unique, repeatable data.'),
    ('Keep DataProvider and test separate',
     'DataProvider classes live in data/. Test logic in tests/. Single responsibility.'),
    ('Cover boundary values',
     'Include min-length, max-length, empty, null cases in your negative DataProviders.'),
    ('Describe each row',
     'The third column (scenario description) prints to console — makes failures easy to trace.'),
    ('Order tests intentionally',
     'Use @Test(priority = N): login first, create resource, then update/delete in sequence.'),
    ('State sharing via JSON',
     'Write IDs to JSON files after creation; read them in subsequent tests. Never chain with globals.'),
]

y = 0.95
for title, desc in practices:
    add_rect(slide, 0.3, y, 0.6, 0.52, ACCENT_BLUE)
    add_text(slide, '✓', 0.3, y + 0.1, 0.6, 0.3,
             font_size=18, bold=True, color=WHITE, align=PP_ALIGN.CENTER)
    add_text(slide, title, 1.0, y + 0.02, 8.7, 0.25,
             font_size=14, bold=True, color=DARK_BLUE)
    add_text(slide, desc, 1.0, y + 0.28, 8.7, 0.2,
             font_size=12, color=DARK_GRAY)
    y += 0.65

# =========================================================
# SLIDE 13 — Summary & Q&A
# =========================================================
slide = add_slide()
add_rect(slide, 0, 0, 10, 5.6, DARK_BLUE)

add_text(slide, 'Summary', 0.5, 0.4, 9, 0.7,
         font_size=32, bold=True, color=WHITE, align=PP_ALIGN.CENTER)

summary = [
    '✓  Data-driven testing = one test method + many data rows',
    '✓  API: @DataProvider returns Object[][] → @Test parameters match by position',
    '✓  Web: Scenario Outline + Examples table = Gherkin data binding',
    '✓  ConfigReader eliminates hardcoded credentials across all tests',
    '✓  Utils.generateRandomString() creates unique, repeatable test data',
    '✓  JSON state files (token.json, ticket_id.json) bridge test class boundaries',
    '✓  Bearer token: login once → save token → all tests reuse it',
]
y = 1.3
for item in summary:
    add_text(slide, item, 0.8, y, 8.5, 0.4,
             font_size=15, color=WHITE)
    y += 0.45

add_rect(slide, 0.3, 5.0, 9.4, 0.45, ACCENT_BLUE)
add_text(slide, 'Q & A  |  GitHub: https://github.com/ariefrahakim/resonance-automation',
         0.5, 5.05, 9, 0.35, font_size=14, color=WHITE, align=PP_ALIGN.CENTER, bold=True)

# =========================================================
# Save
# =========================================================
prs.save(OUTPUT)
print(f'PPT saved: {OUTPUT}')
print(f'Total slides: {len(prs.slides)}')
