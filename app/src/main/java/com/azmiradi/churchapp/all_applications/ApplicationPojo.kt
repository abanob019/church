package com.azmiradi.churchapp.all_applications

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ApplicationPojo")
data class ApplicationPojo(
    var email: String? = null,
    @PrimaryKey
    var nationalID: String="20020020020000",
    var phone: String? = null,
    var jobTitle: String? = null,
    var title: String? = null,
    var name: String? = null,
    var employer: String? = null,
    var className: String? = null,
    var zoneID: String? = null,
    var isApproved: Boolean? = null,
    var note: String? = null,
    var image2: String? = null,
    var image1: String? = null,
    var isAttend: Boolean? = null,
    var row: String? = null,
    var seat: String? = null,
    var recomandedBy: String? = null,
    var priority: Int? = 0,
    var isSandedApproved: Boolean? = null,
    var invitationNumber: String? = null,
    var zoneCode: String? = null,
    var zoneColorName: String? = null,

    )
{
    init {

    }
}
