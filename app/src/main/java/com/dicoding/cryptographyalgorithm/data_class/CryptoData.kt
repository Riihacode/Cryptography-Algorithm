package com.dicoding.cryptographyalgorithm.data_class

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CryptoData(
    val name: String,
    val description: String,
    val photo: Int
) : Parcelable
