package com.example.conexionasincronakotlin

class Result {
    var code = 0 //indica el código de estado devuelto por el servidor web
    lateinit var message: String //información del error
    lateinit var content: String //fichero descargado
}