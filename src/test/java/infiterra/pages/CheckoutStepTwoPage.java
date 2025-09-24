package infiterra.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CheckoutStepTwoPage extends BasePage {

    private final By summaryContainer = By.id("checkout_summary_container");
    private final By lineItem = By.cssSelector("[data-test='inventory-item']");
    private final By itemName = By.cssSelector("[data-test='inventory-item-name']"); 
    private final By itemPrice = By.cssSelector("[data-test='inventory-item-price']"); 
      private final By summaryItemTotal = By.cssSelector("[data-test='subtotal-label']"); 
    private final By summaryTax = By.cssSelector("[data-test='tax-label']");      
    private final By summaryTotal = By.cssSelector("[data-test='total-label']"); 
    private final By finishBtn = By.cssSelector("[data-test='finish']");


    public CheckoutStepTwoPage(WebDriver driver) { super(driver); }

    public boolean isLoaded() {
        return isDisplayed(summaryContainer);
    }

    public List<String> productNames() {
        List<String> names = new ArrayList<>();
        for (var el : driver.findElements(lineItem)) {
            names.add(el.findElement(itemName).getText().trim());
        }
        return names;
    }

    public Map<String, String> productPrices() {
        Map<String, String> map = new HashMap<>();
        for (var el : driver.findElements(lineItem)) {
            map.put(el.findElement(itemName).getText().trim(),
                    el.findElement(itemPrice).getText().trim());
        }
        return map;
    }
    
    public BigDecimal sumProductPrices() {
        BigDecimal sum = BigDecimal.ZERO;
        for (WebElement row : $$(lineItem)) {
            String priceTxt = row.findElement(itemPrice).getText();
            sum = sum.add(money(priceTxt));
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }

  

    public BigDecimal itemTotal() { return money(text(summaryItemTotal)); }
    public BigDecimal tax()       { return money(text(summaryTax)); }
    public BigDecimal total()     { return money(text(summaryTotal)); }


    private BigDecimal money(String text) {
        String num = text.replaceAll("[^0-9.]", "");
        if (num.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
    }
    public void clickFinish() {
    click(finishBtn);
}
}

