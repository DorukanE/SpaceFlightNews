package com.dorukaneskiceri.spaceflightnews.util.ext

fun String.replaceHttpWithHttps(): String {
    return if (this.isBlank()) {
        this
    } else {
        if (this.startsWith("http://")) {
            this.replace("http://", "https://")
        } else {
            this
        }
    }
}