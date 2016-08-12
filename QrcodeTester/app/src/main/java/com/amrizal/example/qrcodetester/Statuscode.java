package com.amrizal.example.qrcodetester;

/**
 * <code>Statuscode</code> This class stores the status codes returned from TheGRID server.
 */
public class Statuscode {

    /**
     * GRID URL is missing
     */
    public static final String URL_MISSING = "URL_MISSING";

    /**
     * Failed to connect to GRIDGOO server
     */
    public static final String FAILED_CONNECT_GRID = "101";

    /**
     * Failed to connect to database
     */
    public static final String FAILED_CONNECT_DB = "102";

    /**
     * Phone tagged with another User ID
     */
    public static final String PHONE_ALREADY_TAGGED = "103";

    /**
     * User ID tagged with another phone
     */
    public static final String USER_ALREADY_TAGGED = "104";

    /**
     * Failed to tag User ID with phone
     */
    public static final String FAILED_TAG_USER = "105";

    /**
     * Failed to untag phone
     */
    public static final String FAILED_UNTAG = "108";

    /**
     * User ID does not exist
     */
    public static final String USERID_NOT_EXIST = "109";

    /**
     * Command reuse
     */
    public static final String COMMAND_REUSE = "110";

    /**
     * Successfully tagged phone with User ID
     */
    public static final String TAG_SUCCESS = "200";

    /**
     * Successfully turned off tagging
     */
    public static final String UNTAG_SUCCESS = "201";

    /**
     * Invalid registration
     */
    public static final String INVALID_REG = "invalidReg";

    /**
     * Registration ID updated successfully
     */
    public static final String REG_ID_UPDATED = "210";

    /**
     * Transaction approved
     */
    public static final String TRANS_APPROVED = "213";

    /**
     * Transaction rejected
     */
    public static final String TRANS_REJECTED = "214";

    /**
     * Invalid JSON format
     */
    public static final String INVALID_REQ = "invalidRequest";

    /**
     * Transaction details retrieved successfully
     */
    public static final String TRANS_DETAIL_RETRIEVED = "211";

    /**
     * Device token does not exist
     */
    public static final String REGID_NOT_EXIST = "121";

    /**
     * Transaction ID does not exist
     */
    public static final String TRANSID_NOT_EXIST = "122";

    /**
     * User ID does not exist (PUSH)
     */
    public static final String USERID_NOT_EXIST_PUSH = "123";
}
