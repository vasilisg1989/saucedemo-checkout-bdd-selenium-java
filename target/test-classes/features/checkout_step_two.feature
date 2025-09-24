Feature: Checkout Step Two Page Validation
  Validate product & price transfer from cart to checkout step two

  Scenario: Products and prices are consistent from cart to checkout step two
    Given I am on the SauceDemo login page
    And I log in as a standard user
    And I add 2 random products to the cart
    And the cart badge should reflect the number of items added
    And I open the cart
    When I click the checkout button
    And I submit checkout step one with first name "Vasilis", last name "Gian", and postal code "11157"
    Then I should be on checkout step two page
    And the same products should be listed as in the cart
    And each product price on checkout step two should equal the cart price
    And the summary totals should be correct
    When I finish the checkout
    Then I should see the order completion message
    And the cart should be empty