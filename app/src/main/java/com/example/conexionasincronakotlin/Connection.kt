package com.example.conexionasincronakotlin

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object Connection {

    @Throws(IOException::class)
    fun connectJava(url: URL?): Result {
        var response = 500
        var result = Result()
        var urlConnection: HttpURLConnection = url?.openConnection() as HttpURLConnection

        response = urlConnection.responseCode
        result.code = response
        if (response == HttpURLConnection.HTTP_OK) {
            result.content = getUrl (urlConnection.inputStream)
        } else {
            result.message = "Error en el acceso a la web: $response"
        }
        urlConnection.disconnect()

        return result
    }

    @Throws(IOException::class)
    private fun getUrl(inputStream: InputStream): String {
        val reader = BufferedReader(inputStream.reader())
        val content = StringBuilder()

        try {
            var line = reader.readLine()
            while (line != null) {
                content.append(line)
                line = reader.readLine()
            }
        } finally {
            reader.close()

            return  content.toString()
        }

    }
}