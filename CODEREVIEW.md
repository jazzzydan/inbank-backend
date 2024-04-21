## Following foundings were discovered and corrected:

### Packages refactored / renamed as follows: ee.taltech renamed to ee.inbank
Run configuration updated

### Swagger dependency added:
// swagger
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0'

### New package dto created to endpoint package. 
DecisionRequest and DecisionResponse classes could be moved into new dto package inside endpoint package.
As I do not have access to project documentation (Confluence or similar) and don't know original project structure, remains as it is.
From my point of view DecisionResponse and Decision classes are redundant and could be merged into one class. 
Did not carry that out as plenty tests would have to be rebuild.

### service package and DecisionEngine service class
From my point of view could be moved to endpoint package. 
As I do not have access to project documentation (Confluence or similar) and don't know original project structure, remains as it is.

### public class DecisionEngineController
Error handling works properly.
As an alternative can be used @ControllerAdvice annotation to handle exceptions globally or @ApiResponse annotation to handle exceptions locally.

### public class DecisionEngine (service package)
There is no need to use long type for loanAmount. String type can be used instead for consistency. 
Not corrected not to break tests integrity.

public Decision calculateApprovedLoan method corrected.
private Decision calculateMaximumLoanSum, private static double calculateCreditScore and private Decision getDecisionWithNewPeriod
methods created.

Commented Code: There are large blocks of code that's commented out. It's better to produce readable code instead of commenting out.

private void verifyInputs method is corrected for better readability (! removed from if statement). Otherwise worked correctly.

### public class DecisionEngineConstants (config package)
Constants were defined correctly.

### TICKET-102 implementation
+ Customer class created to pretend entity handling.
+ Repository, DTO and mapper not produced as there is no real connection to database.
+ DecisionEngineService class updated to handle Customer entity.
+ Country codes and life expectancy added to DecisionEngineConstants class. (even thought this data might be retrieved from DB in real life scenario)
+ AgeConstraintException class created to stay consistent with previously created code.
+ DecisionEngine class method public Decision calculateApprovedLoan updated to handle Customer entity.
+ private boolean customerAgeOutOfRange method created to handle age constraints.
+ as there is no DB, customer country of residency is handled with switch statement. Otherwise it would be a query to DB.
+ private String country = DecisionEngineConstants.ESTONIA; added to Customer class by default.