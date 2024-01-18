package com.example.conexionasincronakotlin

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.conexionasincronakotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.awaitCancellation
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    var start: Long = 0
    var end: Long = 0
    lateinit var myAsyncTask: MyAsyncTask
    lateinit var url: URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.button.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        try {
            url = URL(binding.editText.text.toString())
            if (binding.switch1.isChecked) {
                //descarga usando OkHttp
                OkHTTPdownload(url)
            } else  // descarga usando AsyncTask y Java.net
                download(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            showError(e.message)
        } catch (ex: IOException) {
            showError(ex.message)
        }
    }

    private fun OkHTTPdownload(web: URL) {






    }

    private fun download(url: URL) {
        start = System.currentTimeMillis()
        myAsyncTask = MyAsyncTask(this)
        myAsyncTask.execute(url)
        binding.textView.text = "Descargando la página"
    }

    private fun showError(mensaje: String?) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    inner class MyAsyncTask(private val context: Context) : AsyncTask<URL?, Void?, Result>() {
        private lateinit var progress: ProgressDialog

        override fun onPreExecute() {
            progress = ProgressDialog(context)
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress.setMessage("Conectando  . . . ")
            progress.setCancelable(true)
            progress.setOnCancelListener{cancel (true)}
            progress.show()
        }

        override fun doInBackground(vararg url: URL?): Result {
            lateinit var result: Result

            try {
                // operaciones en el hilo secundario
                result = Connection.connectJava(url[0])
            } catch (e: IOException) {
                // https://www.webfx.com/web-development/glossary/http-status-codes/
                Log.e("HTTP", e.message, e)
                result = Result()
                result.code = 500
                result.message = e.message.toString()
            }

            return result
        }

        override fun onPostExecute(result: Result) {
            progress.dismiss()

            end = System.currentTimeMillis()
            if (result.code == HttpURLConnection.HTTP_OK)
                binding.webView.loadDataWithBaseURL(url.toString(), result.content, "text/html", "UTF-8", null)
            else {
                showError(result.message)
                binding.webView.loadDataWithBaseURL(url.toString(), result.message, "text/html", "UTF-8", null)
            }
            binding.textView.text = "Duración: " + java.lang.String.valueOf(end - start) + " milisegundos"
        }

        override fun onCancelled() {
            progress.dismiss()
            showError("Cancelado")
        }
    }
}