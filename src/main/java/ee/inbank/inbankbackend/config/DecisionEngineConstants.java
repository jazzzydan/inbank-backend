package ee.inbank.inbankbackend.config;

/**
 * Holds all necessary constants for the decision engine.
 */
public class DecisionEngineConstants {
    public static final Integer MINIMUM_LOAN_AMOUNT = 2000;
    public static final Integer MAXIMUM_LOAN_AMOUNT = 10000;
    public static final Integer MAXIMUM_LOAN_PERIOD = 60;
    public static final Integer MINIMUM_LOAN_PERIOD = 12;
    public static final Integer SEGMENT_1_CREDIT_MODIFIER = 100;
    public static final Integer SEGMENT_2_CREDIT_MODIFIER = 300;
    public static final Integer SEGMENT_3_CREDIT_MODIFIER = 1000;

    public static final String ESTONIA = "EE";
    public static final String LATVIA = "LV";
    public static final String LITHUANIA = "LT";

    public static final Integer EE_LIFE_EXPECTANCY = 78;
    public static final Integer LV_LIFE_EXPECTANCY = 75;
    public static final Integer LT_LIFE_EXPECTANCY = 75;
    public static final Integer MINIMUM_AGE = 18;
    public static final Integer AGE_BUFFER = 10;
}
