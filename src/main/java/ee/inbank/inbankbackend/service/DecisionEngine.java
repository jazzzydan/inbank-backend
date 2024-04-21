package ee.inbank.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.inbank.inbankbackend.config.DecisionEngineConstants;
import ee.inbank.inbankbackend.entity.Customer;
import ee.inbank.inbankbackend.exceptions.*;
import org.springframework.stereotype.Service;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
public class DecisionEngine {

    private final EstonianPersonalCodeValidator validator = new EstonianPersonalCodeValidator();

    /**
     * Calculates the maximum loan amount and period for the customer based on their ID code,
     * the requested loan amount and the loan period.
     * The loan period must be between 12 and 60 months (inclusive).
     * The loan amount must be between 2000 and 10000€ months (inclusive).
     *
     * @param personalCode ID code of the customer that made the request.
     * @param loanAmount   Requested loan amount
     * @param loanPeriod   Requested loan period
     * @return A Decision object containing the approved loan amount and period, and an error message (if any)
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException   If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException   If the requested loan period is invalid
     * @throws NoValidLoanException         If there is no valid loan found for the given ID code, loan amount and loan period
     */
    public Decision calculateApprovedLoan(String personalCode, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException,
            NoValidLoanException, AgeConstraintException {
        try {
            verifyInputs(personalCode, loanAmount, loanPeriod);
        } catch (Exception e) {
            return new Decision(null, null, e.getMessage());
        }

        Customer customer = new Customer(personalCode);
        int creditModifier = customer.getCreditModifier();
        if (creditModifier == 0) {
            throw new NoValidLoanException("No valid loan found!");
        } else if (customerAgeOutOfRange(customer.getAge().getYears())) {
            throw new AgeConstraintException("Customer is outside of approved age range!");
        }

        Decision maxSumDecision = calculateMaximumLoanSum(loanPeriod, creditModifier);
        if (maxSumDecision != null) {
            return maxSumDecision;
        } else {
            Decision decisionWithNewPeriod = getDecisionWithNewPeriod(loanPeriod, creditModifier);
            if (decisionWithNewPeriod != null) return decisionWithNewPeriod;
            throw new NoValidLoanException("No valid loan found!");
        }
    }

    private boolean customerAgeOutOfRange(int ageInYears) {
        Customer customer = new Customer();
        String country = customer.getCountry();
        int lifeExpectancy = switch (country) {
            case DecisionEngineConstants.ESTONIA -> DecisionEngineConstants.EE_LIFE_EXPECTANCY;
            case DecisionEngineConstants.LATVIA -> DecisionEngineConstants.LV_LIFE_EXPECTANCY;
            case DecisionEngineConstants.LITHUANIA -> DecisionEngineConstants.LT_LIFE_EXPECTANCY;
            default -> 0;
        };
        return ageInYears < DecisionEngineConstants.MINIMUM_AGE
                || ageInYears > (lifeExpectancy - DecisionEngineConstants.AGE_BUFFER);
    }

    private Decision calculateMaximumLoanSum(int loanPeriod, int creditModifier) {
        int maxLoanSum = DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT;
        int outputLoanAmount;
        int outputLoanPeriod;

        while (maxLoanSum >= DecisionEngineConstants.MINIMUM_LOAN_AMOUNT) {
            double creditScore = calculateCreditScore(loanPeriod, creditModifier, maxLoanSum);
            if (creditScore >= 1.0) {
                outputLoanAmount = maxLoanSum;
                outputLoanPeriod = loanPeriod;
                return new Decision(outputLoanAmount, outputLoanPeriod, null);
            }
            maxLoanSum--;
        }
        return null;
    }

    private static double calculateCreditScore(int loanPeriod, int creditModifier, int maxLoanSum) {
        return (1.0 * creditModifier / maxLoanSum) * loanPeriod;
    }

    private Decision getDecisionWithNewPeriod(int loanPeriod, int creditModifier) {
        Decision maxSumDecision;
        while (loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD) {
            loanPeriod++;
            maxSumDecision = calculateMaximumLoanSum(loanPeriod, creditModifier);
            if (maxSumDecision != null) {
                return maxSumDecision;
            }
        }
        return null;
    }


    /**
     * Verify that all inputs are valid according to business rules.
     * If inputs are invalid, then throws corresponding exceptions.
     *
     * @param personalCode Provided personal ID code
     * @param loanAmount   Requested loan amount
     * @param loanPeriod   Requested loan period
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException   If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException   If the requested loan period is invalid
     */
    private void verifyInputs(String personalCode, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException {

        if (!validator.isValid(personalCode)) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
        if ((DecisionEngineConstants.MINIMUM_LOAN_AMOUNT > loanAmount)
                || (loanAmount > DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT)) {
            throw new InvalidLoanAmountException("Invalid loan amount!");
        }
        if ((DecisionEngineConstants.MINIMUM_LOAN_PERIOD > loanPeriod)
                || (loanPeriod > DecisionEngineConstants.MAXIMUM_LOAN_PERIOD)) {
            throw new InvalidLoanPeriodException("Invalid loan period!");
        }
    }
}