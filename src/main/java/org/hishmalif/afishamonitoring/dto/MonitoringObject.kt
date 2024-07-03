package org.hishmalif.afishamonitoring.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import org.hishmalif.afishamonitoring.model.Partner
import org.hishmalif.afishamonitoring.job.MonitoringException
import org.hishmalif.afishamonitoring.model.MonitoringObjectType

class MonitoringObject {
    data class Request(
        @JsonProperty("id") var id: String,
        @JsonProperty("foreign_id") var foreignId: String,
        @JsonProperty("type") var type: MonitoringObjectType,
        @JsonProperty("slots") var slotForeignIds: List<String>,
        @JsonProperty("partner") var partner: Partner,
        @JsonProperty("date_from") var dateFrom: Instant? = null,
        @JsonProperty("date_to") var dateTo: Instant? = null
    ) {
        constructor(
            id: String,
            foreignId: String,
            type: MonitoringObjectType,
            slotForeignIds: List<String>,
            partner: Partner
        ) : this(id, foreignId, type, slotForeignIds, partner, null, null)

        init {
            if (dateFrom != null && dateTo == null) {
                throw MonitoringException("Не задана дата завершения мониторинга!")
            }
            if (dateFrom != null && (dateTo?.isBefore(Instant.now()) == true) || (dateTo?.isBefore(dateFrom) == true)) {
                throw MonitoringException("Некорректная дата окончания мониторинга!")
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Response(
        @JsonProperty("message") var message: String,
        @JsonProperty("errors") var errors: List<String>? = null,
        @JsonProperty("warnings") var warnings: List<String>? = null,
    ) {
        constructor(message: String) : this(message, null, null)
    }

    data class Message(
        var main: String = ":checkered_flag: Результаты проверки события {} на стороне партнера: :checkered_flag:\n",
        var errors: String = ":alert: Поучены ошибки :alert: \n ```\n{}\n```",
        var warnings: String = ":warning: Полученные предупреждения: \n ```\n{}\n``` :warning: \n",
        var ok: String = ":happy_happy_cat: Во время обработки проблем не найдено :happy_happy_cat:"
    )
}