package org.hishmalif.afishamonitoring.time;

public interface TimeService {
    String sendMessage(String channelId, String text) throws TimeException;
    String sendMessage(String channelId, String text, String rootMessageId) throws TimeException;
    String getChannelId();
}