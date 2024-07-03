package org.hishmalif.afishamonitoring.time;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.hishmalif.afishamonitoring.dto.TimeMessage;
import org.hishmalif.afishamonitoring.config.time.TimeProperties;

@Service
@AllArgsConstructor
public class TimeServiceImpl implements TimeService {
    private final TimeClient client;
    private final TimeProperties properties;

    @Override
    public String sendMessage(String channelId, String text) throws TimeException {
        try {
            TimeMessage.Response response = client.postMessage(new TimeMessage.Request(channelId, text));
            return checkResult(text, response) ? response.getId() : null;
        } catch (Exception e) {
            throw new TimeException("Ошибка при отправке сообщения в Time | Error: {}", e.getMessage());
        }
    }

    @Override
    public String sendMessage(String channelId, String text, String rootMessageId) throws TimeException {
        try {
            TimeMessage.Response response = client.postMessage(new TimeMessage.Request(channelId, text, rootMessageId));
            return checkResult(text, response) ? response.getId() : null;
        } catch (Exception e) {
            throw new TimeException("Ошибка при отправке сообщения в тред | Error: {}", e.getMessage());
        }
    }

    @Override
    public String getChannelId() {
        return properties.getChannelId();
    }

    private boolean checkResult(String text, TimeMessage.Response response) {
        return response.getStatus() == null && response.getMessage().equals(text);
    }
}