package com.rvygon.testapp

import java.util.*

class Recording(uri: String, fileName: String, isPlaying: Boolean, date:String) {

    var uri: String
        internal set
    var fileName: String
        internal set
    var isPlaying = false
    var date: String

    init {
        this.uri = uri
        this.date = date
        this.fileName = fileName
        this.isPlaying = isPlaying
    }

}