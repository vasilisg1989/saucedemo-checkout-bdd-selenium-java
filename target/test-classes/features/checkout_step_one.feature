Feature: Checkout Step One Error Validation
  As a shopper on SauceDemo
  I want required field validation on checkout step one
  So that I cannot proceed with missing mandatory info

  Scenario Outline: Prevent continue with a missing required field
    Given I am on the SauceDemo login page
    And I log in as a standard user
    And I add any product to the cart
    And I open the cart
    When I click the checkout button
    And I submit checkout step one with first name "<first>", last name "<last>", and postal code "<zip>"
    Then I should see an error message "<error>"
    And I should remain on checkout step one page

    Examples:
      | first | last | zip   | error                          |
      |       | Gian  | 11157 | Error: First Name is required  |
      | Vasilis  |      | 11157 | Error: Last Name is required   |
      | Vasilis  | Gian  |       | Error: Postal Code is required |
