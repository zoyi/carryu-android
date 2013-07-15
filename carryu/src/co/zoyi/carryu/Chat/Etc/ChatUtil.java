package co.zoyi.carryu.Chat.Etc;

public class ChatUtil {
    public static class User {
        public static String getUserIDFromJabberID(String jabberID) {
            jabberID = jabberID.replace("sum", "");
            jabberID = jabberID.substring(0, jabberID.indexOf("@"));
            return jabberID;
        }

        public static boolean isEqualUser(String firstJabberID, String secondJabberId) {
            return getUserIDFromJabberID(firstJabberID).equals(getUserIDFromJabberID(secondJabberId));
        }

        public static String toGameClientJabberId(String jabberID) {
            return jabberID.replace("Smack", "xiff");
        }
    }
}
