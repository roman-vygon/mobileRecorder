package com.rvygon.testapp

class Recording(uri: String, fileName: String, isPlaying: Boolean) {

    var uri: String
        internal set
    var fileName: String
        internal set
    var isPlaying = false


    init {
        this.uri = uri
        this.fileName = fileName
        this.isPlaying = isPlaying
    }

}