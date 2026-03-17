package playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookstoreTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(System.getProperty("headless", "false").equals("true")));
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

    private void navigateToProduct() {
        page.navigate("https://depaul.bncollege.com/search/?text=earbuds");
        page.waitForTimeout(2000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand").getByRole(AriaRole.LIST).locator("label")
            .filter(new Locator.FilterOptions().setHasText("brand JBL (10)")).click();
        page.waitForTimeout(1000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("Color Black (5)")).click();
        page.waitForTimeout(1000);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.locator("#facet-price").getByRole(AriaRole.IMG).click();
        page.waitForTimeout(1000);
        page.getByTitle("JBL Quantum True Wireless").first().click();
        page.waitForTimeout(2000);
    }

    private void addToCartAndGoToCart() {
        navigateToProduct();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart")).click();
        page.waitForTimeout(4000);
        page.navigate("https://depaul.bncollege.com/cart");
        page.waitForTimeout(2000);
    }

    private void proceedToCheckout() {
        addToCartAndGoToCart();
        page.locator("label").filter(new Locator.FilterOptions().setHasText("FAST In-Store Pickup")).click();
        page.waitForTimeout(1000);
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();
        page.waitForTimeout(2000);
        page.getByLabel("Proceed To Checkout").click();
        page.waitForTimeout(2000);
    }

    private void proceedAsGuest() {
        proceedToCheckout();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
        page.waitForTimeout(2000);
    }

    private void fillContactInfo() {
        proceedAsGuest();
        page.getByPlaceholder("Please enter your first name").fill("John");
        page.getByPlaceholder("Please enter your last name").fill("Doe");
        page.getByPlaceholder("Please enter a valid email").fill("john.doe@test.com");
        page.getByPlaceholder("Please enter a valid phone").fill("3125551234");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(3000);
    }

    @Test
    @DisplayName("TestCase Bookstore")
    void testBookstore() {
        navigateToProduct();

        // Assert product details
        assertThat(page.locator("h1.name").first()).containsText("JBL Quantum True Wireless");
        assertThat(page.locator(".sku").first()).isAttached();
        assertThat(page.locator("[class*='price']").first()).isAttached();
        assertThat(page.locator("[class*='description']").first()).isAttached();

        // Add to cart
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart")).click();
        page.waitForTimeout(4000);
        assertThat(page.locator("[class*='cart']").first()).isAttached();

        // Click cart
        page.navigate("https://depaul.bncollege.com/cart");
    }

    @Test
    @DisplayName("TestCase Your Shopping Cart Page")
    void testShoppingCart() {
        addToCartAndGoToCart();

        // Assert shopping cart page
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your Shopping Cart"))).isAttached();
        assertThat(page.getByText("JBL Quantum True Wireless").first()).isAttached();
        assertThat(page.locator("[class*='price']").first()).isAttached();

        // Select FAST In-Store Pickup
        page.locator("label").filter(new Locator.FilterOptions().setHasText("FAST In-Store Pickup")).click();
        page.waitForTimeout(1000);

        // Assert sidebar totals attached

        // Enter promo code
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();
        page.waitForTimeout(2000);

        // Assert promo code rejected
        assertThat(page.locator("[class*='error'], [class*='invalid']").first()).isAttached();

        // Proceed to checkout
        page.getByLabel("Proceed To Checkout").click();
        page.waitForTimeout(2000);
    }

    @Test
    @DisplayName("TestCase Create Account Page")
    void testCreateAccountPage() {
        proceedToCheckout();

        // Assert Create Account label
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Create Account"))).isAttached();

        // Proceed as guest
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
        page.waitForTimeout(2000);
    }

    @Test
    @DisplayName("TestCase Contact Information Page")
    void testContactInformationPage() {
        proceedAsGuest();

        // Assert Contact Information page
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Contact Information"))).isAttached();

        // Fill in contact info
        page.getByPlaceholder("Please enter your first name").fill("John");
        page.getByPlaceholder("Please enter your last name").fill("Doe");
        page.getByPlaceholder("Please enter a valid email").fill("john.doe@test.com");
        page.getByPlaceholder("Please enter a valid phone").fill("3125551234");


        // Click continue
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(2000);
    }

    @Test
    @DisplayName("TestCase Pickup Information")
    void testPickupInformation() {
        fillContactInfo();

        // Assert contact info displayed
        assertThat(page.getByText("John Doe")).isAttached();
        assertThat(page.getByText("john.doe@test.com")).isAttached();

        // Assert pickup location
        assertThat(page.locator(".bned-entries-delivery-multicampus-name").first()).isAttached();

        // Assert pickup person
        assertThat(page.getByText("I'll pick them up")).isAttached();

        // Assert sidebar totals

        // Assert pickup item
        assertThat(page.getByText("JBL Quantum True Wireless").first()).isAttached();

        // Click continue
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(3000);
    }

    @Test
    @DisplayName("TestCase Payment Information")
    void testPaymentInformation() {
        fillContactInfo();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForTimeout(3000);

        // Assert payment page
        assertThat(page.locator("#checkoutOrderDetails").first()).isAttached();
        assertThat(page.getByText("JBL Quantum True Wireless").first()).isAttached();

        // Go back to cart
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back To Cart")).click();
        page.waitForTimeout(2000);
    }

    @Test
    @DisplayName("TestCase Your Shopping Cart - Delete")
    void testDeleteFromCart() {
        addToCartAndGoToCart();

        // Delete product from cart
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove")).first().click();
        page.waitForTimeout(3000);

        // Assert cart is empty
        assertThat(page.getByText("Your cart is empty")).isAttached();
    }
}
