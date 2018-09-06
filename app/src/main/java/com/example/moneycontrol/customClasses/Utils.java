package com.example.moneycontrol.customClasses;

import java.text.DecimalFormat;

public class Utils {

    // INTENT EXTRAS
    public static final String INTENT_EXTRA_CATEGORY_NAME = "category_name";
    public static final String INTENT_EXTRA_SUBCATEGORY   = "choose_subcategory";

    // REQUEST CODES
    public static final int         REQUEST_CODE_CHOOSE_CATEGORY        = 0;
    public static final int         REQUEST_CODE_CHOOSE_SUBCATEGORY     = 1;
    public static final int         REQUEST_CODE_ADD_TRANSACTION        = 2;

    // PREFERENCES KEYS
    public static final String      PREFERENCES_PASSWORD                            = "password";
    public static final String      PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN         = "use_fingerprint";
    public static final String      PREFERENCES_IS_FINGERPRINT_SET                  = "is_fingerprint_set";
    public static final String      PREFERENCES_FINGERPRINT                         = "fingerprint";

    public static final String      TEMP_FINGERPRINT_KEY_NAME           = "temp_fingerprint_key";
    public static final String      FINGERPRINT_KEY_NAME                = "fingerprint_key";
    public static final String      FINGERPRINT_NOT_ALLOWED             = "fingerprint_not_allowed";

    public static final String      INTENT_CATEGORY_ID                  = "category_id";

    public static String formatAmount(String amount){

        try {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            return decimalFormat.format(Double.parseDouble(amount));
        }catch (Exception e){
            e.printStackTrace();
        }
       return amount;
    }
}
