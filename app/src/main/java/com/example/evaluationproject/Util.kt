package com.example.evaluationproject

class Util {
    companion object {
        @JvmField
        var evaNameUtil: String = "№0000001"
        var folderNameUtil: String = "№0000001"
        var filesArraylist = ArrayList<String>()
        @JvmStatic lateinit var instance: Util
    }

    init {
        instance = this
    }
}