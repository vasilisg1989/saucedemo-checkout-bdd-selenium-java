package steps;

import io.cucumber.java.en.*;
import org.testng.Assert;
import infiterra.pages.*;
import infiterra.support.DriverFactory;
import infiterra.support.Config;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CheckoutSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutStepOnePage stepOnePage;
    private CheckoutStepTwoPage stepTwoPage;
    private CheckoutCompletePage completePage;

    // shared state for validations
    private List<String> cartNames;
    private Map<String, String> cartPrices;
    private int lastAddedCount = 0;

    @Given("I am on the SauceDemo login page")
    public void i_am_on_login_page() {
        driver = DriverFactory.getDriver();
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        stepOnePage = new CheckoutStepOnePage(driver);
        stepTwoPage = new CheckoutStepTwoPage(driver);
        completePage  = new CheckoutCompletePage(driver);
        loginPage.open();
    }

    @And("I log in as a standard user")
    public void login_as_standard_user() {
        loginPage.login(Config.swagUser(), Config.swagPass());
        Assert.assertTrue(inventoryPage.isLoaded(), "Inventory page did not load after login");
        try { DriverFactory.getDriver().switchTo().activeElement().sendKeys(Keys.ESCAPE); } catch (Exception ignored) {}
    }

    @And("I add any product to the cart")
    public void add_any_product() {
        inventoryPage.addAnyProductToCart();
    }

    @And("I add {int} random products to the cart")
    public void add_random_products(Integer count) {
        inventoryPage.addRandomProductsToCart(count);
        lastAddedCount = count;
    }
    @And("the cart badge should reflect the number of items added")
public void cart_badge_should_reflect_number_of_items_added() {
    int actual = inventoryPage.cartBadgeCount();
    Assert.assertEquals(actual, lastAddedCount,
        "Cart badge does not match items just added");
}

    @And("I open the cart")
    public void open_cart() {
        inventoryPage.openCart();
         cartPage.waitUntilLoaded(); 
         cartNames= cartPage.productNames();     
         cartPrices= cartPage.productPrices();
    }

    @When("I click the checkout button")
    public void click_checkout() {
        cartNames = cartPage.getProductNames();
        cartPrices = cartPage.getProductPrices();
        cartPage.clickCheckout();
    }

@And("I submit checkout step one with first name {string}, last name {string}, and postal code {string}")
public void submit_step_one_with(String first, String last, String zip) {
    stepOnePage.submit(first, last, zip);
}

    @Then("I should see an error message {string}")
    public void should_see_error(String expected) {
        Assert.assertEquals(stepOnePage.getError(), expected);
    }

    @And("I should remain on checkout step one page")
    public void remain_on_step_one() {
        Assert.assertTrue(stepOnePage.isLoaded(), "Expected to remain on Checkout Step One page");
    }


    @Then("I should be on checkout step two page")
    public void on_step_two() {
        Assert.assertTrue(stepTwoPage.isLoaded(), "Checkout step two not loaded");
    }

    @And("the same products should be listed as in the cart")
    public void same_products_as_cart() {
        List<String> step2Names = stepTwoPage.productNames();
        Assert.assertEqualsNoOrder(step2Names.toArray(), cartNames.toArray(), "Product names differ between cart and step two");
    }

    @And("each product price on checkout step two should equal the cart price")
    public void prices_match() {
        Map<String, String> step2Prices = stepTwoPage.productPrices();
        for (var entry : cartPrices.entrySet()) {
            String name = entry.getKey();
            String cartPrice = entry.getValue();
            Assert.assertTrue(step2Prices.containsKey(name), "Missing product on step two: " + name);
            Assert.assertEquals(step2Prices.get(name), cartPrice, "Price mismatch for " + name);
        }
    }
  @And("the summary totals should be correct")
public void summary_totals_should_be_correct() {
    
    BigDecimal sum       = stepTwoPage.sumProductPrices();
    BigDecimal itemTotal = stepTwoPage.itemTotal();
    BigDecimal tax       = stepTwoPage.tax();
    BigDecimal total     = stepTwoPage.total();

   
   Assert.assertEquals(itemTotal, sum, "Item total != sum of item prices on step two");

    BigDecimal expectedTotal = itemTotal.add(tax).setScale(2, RoundingMode.HALF_UP);
    Assert.assertEquals(total, expectedTotal, "Total != item total + tax");

    
    Assert.assertTrue(tax.compareTo(BigDecimal.ZERO) >= 0, "Tax should be non-negative");
}
@When("I finish the checkout")
public void i_finish_the_checkout() {
    stepTwoPage.clickFinish();
}

// --- Completion page checks ---
@Then("I should see the order completion message")
public void i_should_see_the_order_completion_message() {
    Assert.assertTrue(completePage.isLoaded(), "Completion page not loaded");
    Assert.assertEquals(
            completePage.headerText(), "Thank you for your order!");
    Assert.assertTrue(
            completePage.bodyText().contains("will arrive just as fast as the pony"),
            "Completion body text not as expected");
}

@And("the cart should be empty")
public void the_cart_should_be_empty() {
    int badge = inventoryPage.cartBadgeCount();
    Assert.assertEquals(badge, 0, "Cart badge is not empty (0)");
}
}
