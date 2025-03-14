package org.teamvoided.creative_works.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder


val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
fun String.toJsonString(): String = GSON.toJson(this)
