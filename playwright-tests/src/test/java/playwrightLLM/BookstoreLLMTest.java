package playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class BookstoreLLMTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(System.getProperty("headless", "false").equals("true")));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
            .setRecordVideoDir(Paths.get("videos/"))
            .setRecordVideoSize(1280, 720));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    @DisplayName("LLM Generated - Search, Filter, Add to Cart, Verify Cart")
    void testBookstoreFlow() {
        // 1) Open site and search for earbuds via URL
        page.navigate("https://depaul.bncollege.com/search/?text=earbuds");
        page.waitForTimeout(2000);
        PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile(".*depaul\\.bncollege\\.com/search/.*"));

        // 2) Filter Brand = JBL
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("label")
            .filter(new Locator.FilterOptions().setHasText("JBL"))
            .first()
            .click();
        page.waitForTimeout(1000);

        // 3) Filter Color = Black
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.locator("label")
            .filter(new Locator.FilterOptions().setHasText("Color Black"))
            .first()
            .click();
        page.waitForTimeout(1000);

        // 4) Filter Price = Over $50
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.locator("label")
            .filter(new Locator.FilterOptions().setHasText("Price Over $50"))
            .first()
            .click();
        page.waitForTimeout(1000);

        // 5) Click product: JBL Quantum True Wireless
        page.getByRole(AriaRole.LINK,
            new Page.GetByRoleOptions()
                .setName(Pattern.compile("JBL Quantum True Wireless.*", Pattern.CASE_INSENSITIVE)))
            .first().click();
        page.waitForTimeout(2000);

        // 6) Add to cart
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart")).click();
        page.waitForTimeout(4000);

        // 7) Verify cart has 1 item
        PlaywrightAssertions.assertThat(page.locator("a[href='/cart']").first()).containsText("1");

        // 8) Go to cart page
        page.locator("a[href='/cart']").first().click();
        page.waitForTimeout(2000);

        // 9) Verify cart page shows "Your Shopping Cart"
        PlaywrightAssertions.assertThat(
            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your Shopping Cart"))
        ).isAttached();
    }
}
