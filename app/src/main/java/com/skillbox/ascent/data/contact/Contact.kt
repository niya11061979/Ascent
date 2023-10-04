package com.skillbox.ascent.data.contact

data class Contact(
    val id: Long,
    val name: String,
    val contactPhotoUri: String,
    val phones: List<String>
)