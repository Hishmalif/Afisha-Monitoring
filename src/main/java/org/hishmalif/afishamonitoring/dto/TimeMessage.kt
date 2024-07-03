package org.hishmalif.afishamonitoring.dto

import com.fasterxml.jackson.annotation.JsonProperty

class TimeMessage {
    data class Request(
        @JsonProperty("channel_id") var channelId: String,
        @JsonProperty("message") var message: String,
        @JsonProperty("root_id") var rootMessageId: String? = null
    ) {
        constructor(channelId: String, message: String) : this(channelId, message, null)
    }

    data class Response(
        @JsonProperty("id") var id: String,
        @JsonProperty("message") var message: String,
        @JsonProperty("status_code") var status: String? = null
    )
}