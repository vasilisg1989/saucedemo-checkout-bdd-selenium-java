package infiterra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryPage extends BasePage {

    private final By inventoryContainer = By.cssSelector("a.shopping_cart_link");
    private final By inventoryItems = By.cssSelector(".inventory_item");
    private final By itemName = By.cssSelector(".inventory_item_name");
    private final By itemPrice = By.cssSelector("[data-test='inventory-item-price']"); 
    private final By addToCartBtn = By.cssSelector("button.btn_inventory");
    private final By cartLink = By.id("shopping_cart_container");
    private final By cartBadge = By.cssSelector("[data-test='shopping-cart-badge']");

    public InventoryPage(WebDriver driver) { super(driver); }

    public boolean isLoaded() {
        return isDisplayed(inventoryContainer);
    }

    public void addAnyProductToCart() {
        click(addToCartBtn); // προσθέτει το πρώτο διαθέσιμο
    }

    public List<String> addRandomProductsToCart(int count) {
        var items = driver.findElements(inventoryItems);
        Collections.shuffle(items);
        var picked = items.stream().limit(count).collect(Collectors.toList());
        List<String> names = new ArrayList<>();
        for (var el : picked) {
            String name = el.findElement(itemName).getText().trim();
            names.add(name);
            el.findElement(addToCartBtn).click();
        }
        return names;
    }

    public Map<String, String> readAllItemPrices() {
        Map<String, String> map = new HashMap<>();
        var items = driver.findElements(inventoryItems);
        for (var el : items) {
            String name = el.findElement(itemName).getText().trim();
            String price = el.findElement(itemPrice).getText().trim();
            map.put(name, price);
        }
        return map;
    }

    public void openCart() {
        click(cartLink);
    }
    public int cartBadgeCount() {
    if (isDisplayed(cartBadge)) {
        String txt = text(cartBadge).trim();
        try { return Integer.parseInt(txt); }
        catch (NumberFormatException e) { return 0; }
    }
    return 0; 
}
}
