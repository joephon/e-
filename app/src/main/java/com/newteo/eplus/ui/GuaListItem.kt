package com.newteo.eplus.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GuaListItem(
    var picId: Int = 0,
    val id: Int,
    var title: String,
    val tip: String,
    val hint: String,
    val des: String,
    val yaoCodeList: List<Int>,
    val yaoList: List<String>,
    val details: List<String>,
) : Parcelable