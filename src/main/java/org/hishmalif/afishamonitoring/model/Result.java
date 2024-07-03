package org.hishmalif.afishamonitoring.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hishmalif.afishamonitoring.utill.ParseMessage;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Result {
    private final ResultTypes type;
    private String message;

    public Result(ResultTypes type, String message, Object... objects) {
        this.type = type;
        this.message = ParseMessage.getMessage(message, objects);
    }
}