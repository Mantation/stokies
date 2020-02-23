package properties;

public class accessKeys {
    private static String shop;
    private static String name;
    private static String surname;
    private static String stokvel;
    private static String location;
    private static String phone;
    private static String altPhone;
    private static String defaultUserId;
    private static String defaultUserEmail;
    private static boolean authenticated;
    private static boolean networkUnAvailable;
    private static boolean loggedin;
    private static boolean exitApplication;
    private static String messagingToken;

    public static String getShop() {
        return shop;
    }

    public static void setShop(String shop) {
        accessKeys.shop = shop;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        accessKeys.name = name;
    }

    public static String getSurname() {
        return surname;
    }

    public static void setSurname(String surname) {
        accessKeys.surname = surname;
    }

    public static String getStokvel() {
        return stokvel;
    }

    public static void setStokvel(String stokvel) {
        accessKeys.stokvel = stokvel;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        accessKeys.location = location;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        accessKeys.phone = phone;
    }

    public static String getAltPhone() {
        return altPhone;
    }

    public static void setAltPhone(String altPhone) {
        accessKeys.altPhone = altPhone;
    }

    public static String getDefaultUserId() {
        return defaultUserId;
    }

    public static void setDefaultUserId(String defaultUserId) {
        accessKeys.defaultUserId = defaultUserId;
    }

    public static boolean isAuthenticated() {
        return authenticated;
    }

    public static void setAuthenticated(boolean authenticated) {
        accessKeys.authenticated = authenticated;
    }

    public static boolean isNetworkUnAvailable() {
        return networkUnAvailable;
    }

    public static void setNetworkUnAvailable(boolean networkUnAvailable) {
        accessKeys.networkUnAvailable = networkUnAvailable;
    }

    public static String getDefaultUserEmail() {
        return defaultUserEmail;
    }

    public static void setDefaultUserEmail(String defaultUserEmail) {
        accessKeys.defaultUserEmail = defaultUserEmail;
    }

    public static boolean isLoggedin() {
        return loggedin;
    }

    public static void setLoggedin(boolean loggedin) {
        accessKeys.loggedin = loggedin;
    }

    public static boolean isExitApplication() {
        return exitApplication;
    }

    public static void setExitApplication(boolean exitApplication) {
        accessKeys.exitApplication = exitApplication;
    }

    public static String getMessagingToken() {
        return messagingToken;
    }

    public static void setMessagingToken(String messagingToken) {
        accessKeys.messagingToken = messagingToken;
    }
}
