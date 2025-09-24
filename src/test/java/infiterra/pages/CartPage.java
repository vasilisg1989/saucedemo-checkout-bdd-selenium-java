package infiterra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;

public class CartPage extends BasePage {

    
    private final By cartItem      = By.cssSelector(".cart_item");
    private final By itemName      = By.cssSelector(".inventory_item_name");
    private final By itemPrice     = By.cssSelector(".inventory_item_price");
    private final By checkoutBtn   = By.id("checkout");

    public CartPage(WebDriver driver) { super(driver); }

   
    public boolean isLoaded() { return isDisplayed(checkoutBtn); }

    public void waitUntilLoaded() { $(checkoutBtn); }

   
    public void clickCheckout() {
       
        waitUntilLoaded();
        click(checkoutBtn);
    }

   
    public List<String> productNames() {
        List<String> names = new ArrayList<>();
        for (WebElement el : $$(cartItem)) {
            names.add(el.findElement(itemName).getText().trim());
        }
        return names;
    }

    
    public List<String> getProductNames() { return productNames(); }

   
    public Map<String, String> productPrices() {
        Map<String, String> map = new LinkedHashMap<>();
        for (WebElement el : $$(cartItem)) {
            String name  = el.findElement(itemName).getText().trim();
            String price = el.findElement(itemPrice).getText().trim();
            map.put(name, price);
        }
        return map;
    }

  
    public Map<String, String> getProductPrices() { return productPrices(); }
}
