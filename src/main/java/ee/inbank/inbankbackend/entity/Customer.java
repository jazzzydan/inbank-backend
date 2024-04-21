package ee.inbank.inbankbackend.entity;

import com.github.vladislavgoltjajev.personalcode.exception.PersonalCodeException;
import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeParser;
import ee.inbank.inbankbackend.config.DecisionEngineConstants;
import ee.inbank.inbankbackend.exceptions.InvalidPersonalCodeException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private String firstName;
    private String lastName;
    private String personalCode;
    private EstonianPersonalCodeParser codeParser;
    private Period age;
    private int creditModifier;
    private String country = DecisionEngineConstants.ESTONIA;

    public Customer(String personalCode) throws InvalidPersonalCodeException {
        this.personalCode = personalCode;
        this.codeParser = new EstonianPersonalCodeParser();
        try {
            this.age = codeParser.getAge(personalCode);
        } catch (PersonalCodeException e) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
        this.creditModifier = calculateCreditModifier(personalCode);
    }

    /**
     * Calculates the credit modifier of the customer to according to the last four digits of their ID code.
     * Debt - 0000...2499
     * Segment 1 - 2500...4999
     * Segment 2 - 5000...7499
     * Segment 3 - 7500...9999
     *
     * @param personalCode ID code of the customer that made the request.
     * @return Segment to which the customer belongs.
     */
    private int calculateCreditModifier(String personalCode) {
        int segment = Integer.parseInt(personalCode.substring(personalCode.length() - 4));

        if (segment < 2500) {
            return 0;
        } else if (segment < 5000) {
            return DecisionEngineConstants.SEGMENT_1_CREDIT_MODIFIER;
        } else if (segment < 7500) {
            return DecisionEngineConstants.SEGMENT_2_CREDIT_MODIFIER;
        }

        return DecisionEngineConstants.SEGMENT_3_CREDIT_MODIFIER;
    }

}
