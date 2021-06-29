package com.example.bookstore.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookstore.R
import com.example.bookstore.database.BookDatabase
import com.example.bookstore.database.BookEntity
import com.example.bookstore.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName :TextView
    lateinit var txtBookAuthor :TextView
    lateinit var txtBookPrice :TextView
    lateinit var txtBookRating :TextView
    lateinit var imgBookImage :ImageView
    lateinit var txtBookDesc : TextView
    lateinit var btnAddToFav :Button
    lateinit var progressBar : ProgressBar
    lateinit var progressLayout : RelativeLayout
    lateinit var toolbar: Toolbar

    var bookId : String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDec)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent!=null){
            bookId = intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
        }

        if(bookId=="100"){
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book"

        val jsonParams = JSONObject()
        jsonParams.put("book_id",bookId)

        if (ConnectionManager().checkConnection(this@DescriptionActivity)){
            val jsonRequest = object :JsonObjectRequest(Method.POST,url,jsonParams,Response.Listener {

                try {

                    val success = it.getBoolean("success")
                    if(success){
                        val bookJsonObject = it.getJSONObject("book_data")
                        progressLayout.visibility = View.GONE

                        val bookImageUrl= bookJsonObject.getString("image")
                        Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                        txtBookName.text = bookJsonObject.getString("name")
                        txtBookAuthor.text = bookJsonObject.getString("author")
                        txtBookRating.text = bookJsonObject.getString("rating")
                        txtBookDesc.text = bookJsonObject.getString("description")

                        val bookEntity=BookEntity(
                            bookId?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRating.text.toString(),
                            txtBookDesc.text.toString(),
                            bookImageUrl
                            )

                        val checkFav = DBAsyncTask(applicationContext,bookEntity,1).execute()
                        val isFav = checkFav.get()

                        if (isFav){
                            btnAddToFav.text = "Remove from Favourite"
                            val  favColor = ContextCompat.getColor(applicationContext,R.color.favourite)
                            btnAddToFav.setBackgroundColor(favColor)
                        }else{
                            btnAddToFav.text = "Add to Favourite"
                            val  favColor = ContextCompat.getColor(applicationContext,R.color.purple_500)
                            btnAddToFav.setBackgroundColor(favColor)
                        }

                        btnAddToFav.setOnClickListener{
                            if (!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                                val async = DBAsyncTask(applicationContext,bookEntity,2).execute()
                                val  result = async.get()
                                if (result){
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Book added to favourite",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnAddToFav.text = "Remove from Favourites"
                                    val  favColor = ContextCompat.getColor(applicationContext,R.color.favourite)
                                    btnAddToFav.setBackgroundColor(favColor)
                                }else{
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Some error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }else{
                                val async = DBAsyncTask(applicationContext,bookEntity,3).execute()
                                val  result = async.get()

                                if (result){
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Book removed from favourite",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnAddToFav.text = "Add from Favourites"
                                    val  favColor = ContextCompat.getColor(applicationContext,R.color.purple_500)
                                    btnAddToFav.setBackgroundColor(favColor)
                                }else{
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Some error occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }

                    }
                    else{
                        Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                    }

                }catch (e:Exception){
                    Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener{
                Toast.makeText(this@DescriptionActivity,"Volley error $it",Toast.LENGTH_SHORT).show()
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "a087f3bb8710df"
                    return headers
                }
            }
            queue.add(jsonRequest)
        }else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){ text, listener->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }



    }

    class DBAsyncTask(val context: Context,val bookEntity: BookEntity, val mode:Int) : AsyncTask<Void, Void, Boolean>() {
        /*
        Mode 1 -> check if the book is favourite or not
        Mode 2 -> save the book into DB as favourite
        Mode 3 -> Remove the favourite book
         */
        val db = Room.databaseBuilder(context,BookDatabase::class.java,"book-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1->{
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!= null
                }
                2->{
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3->{
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}