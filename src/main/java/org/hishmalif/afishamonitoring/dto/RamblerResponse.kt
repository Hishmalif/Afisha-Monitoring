package org.hishmalif.afishamonitoring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

class RamblerResponse {
    data class Place(
        @JsonProperty("Id") var id: String,
        @JsonProperty("CityId") var cityId: String
    )

    data class Event(
        @JsonProperty("Id") var id: String,
        @JsonProperty("Description") var description: String? = null,
        @field:JsonProperty("Images") var _images: List<Image>? = ArrayList()  // Используем @field:JsonProperty для применения к полю
    ) {
        @get:JsonProperty("Images")
        val images: List<Image>
            get() = _images ?: ArrayList()
    }

    data class Image(
        @JsonProperty("Url") var url: String,
        @JsonProperty("Type") var type: String?
    ) {
        fun isPoster() = "Poster" == type
    }

    data class Schedule(
        @JsonProperty("Place") var place: Place,
        @JsonProperty("Sessions") var sessions: List<Session>
    )

    data class Session(
        @JsonProperty("Id") var id: String,
        @JsonProperty("SaleEndDateTime") var saleEndDate: ZonedDateTime,
        @JsonProperty("SessionDateTime") var slotStartDate: ZonedDateTime,
        @JsonProperty("IsSaleAvailable") var isSaleAvailable: Boolean,
        @JsonProperty("MinPrice") var minPrice: Double? = null,
        @JsonProperty("MaxPrice") var maxPrice: Double? = null,
        @JsonProperty("AvailableTicketsCount") var availableTicketsCount: Int? = 0
    )

    data class Scheme(
        @JsonProperty("Id") var id: String
    )
}