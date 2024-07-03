package org.hishmalif.afishamonitoring.utill;

public class ParseMessage {
    public static String getMessage(String message, Object... objects) {
        for (Object o : objects) {
            message = message.replaceFirst("\\{}", o.toString());
        }
        return message;
    }
}