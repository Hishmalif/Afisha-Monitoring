package org.hishmalif.afishamonitoring.model

import java.time.ZonedDateTime

data class Event(val id: String) {
    var description: String? = null
    var imageUrl: String? = ""
    var schedulesPlaces: List<Place> = ArrayList()

    data class Place(
        val id: String,
        val cityId: String,
        var schedules: List<Schedule>? = null
    ) {
        constructor(id: String, city: String) : this(id, city, null)
    }

    data class Schedule(
        var id: String,
        var saleEndDate: ZonedDateTime,
        var slotStartDate: ZonedDateTime,
        var isSaleAvailable: Boolean,
        var minPrice: Double? = null,
        var maxPrice: Double? = null,
        var availableTicketsCount: Int? = null,
        var schema: List<String>? = ArrayList()
    )
}