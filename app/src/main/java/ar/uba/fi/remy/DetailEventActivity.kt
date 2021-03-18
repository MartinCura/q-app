package ar.uba.fi.remy

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.uba.fi.remy.model.Friend
import ar.uba.fi.remy.model.FriendAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_detail_event.*
import kotlinx.android.synthetic.main.dialog_list_friends.*
import android.widget.LinearLayout
import android.widget.TextView
import ar.uba.fi.remy.ui.loadingIndicator.LoadingIndicatorFragment
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class DetailEventActivity : AppCompatActivity() {
    lateinit var token: String
    lateinit var adapter: FriendAdapter
    lateinit var idInvitados: MutableList<Int>
    var idEvento: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)

        //Obtener token
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        token = sharedPref?.getString("TOKEN", "")!!

        idEvento = intent.getIntExtra("id_evento", 0)

        LoadingIndicatorFragment.show(this)
        loadEventData()

        loadReommendedRecipes()

        event_add_person_btn.setOnClickListener(View.OnClickListener {
            loadFriends()
        })
    }

    private fun loadReommendedRecipes() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/recommend/recipes/event/?id=" + idEvento + "&need_all_ingredients=false"
        val dynamicContent = findViewById<View>(R.id.dynamic_recommendations) as LinearLayout


        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")
                var recommendations = response.getJSONArray("results")
                for (i in 0 until recommendations.length()) {
                    var recommendation = recommendations.getJSONObject(i)
                    var wizardView = layoutInflater.inflate(R.layout.recommended_item, dynamicContent, false)
                    val textView = wizardView.findViewById(R.id.title) as TextView
                    textView.text = recommendation.getJSONObject("recipe").getString("title")
                    // Ocultar estrellas de difucultad de forma dinamica
                    val score = recommendation.getString("rating").toDouble()
                    val starTwo: ImageView = wizardView.findViewById(R.id.starTwo)
                    val starThree: ImageView = wizardView.findViewById(R.id.starThree)
                    val starFour: ImageView = wizardView.findViewById(R.id.starFour)
                    val starFive: ImageView = wizardView.findViewById(R.id.starFive)
                    if(score < 5) starFive.visibility = View.GONE
                    if(score < 4) starFour.visibility = View.GONE
                    if(score < 3) starThree.visibility = View.GONE
                    if(score < 2) starTwo.visibility = View.GONE

                    val ivFoto:ImageView = wizardView.findViewById(R.id.foto)
                    var img = recommendation.getJSONObject("recipe").getString("description").split('\'')[3]
                    if(img.isNotEmpty()) {
                        Picasso.get()
                            .load(img.replace("http", "https"))
                            .transform(RoundedCornersTransformation(60,0))
                            .resize(200, 200).into(ivFoto)
                    }

                    wizardView.setOnClickListener {
                        val intent = Intent(this, DetailRecipeActivity::class.java)
                        intent.putExtra("id_receta", recommendation.getJSONObject("recipe").getInt("id"))
                        startActivity(intent)
                    }

                    dynamicContent.addView(wizardView)
                }
                LoadingIndicatorFragment.hide()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
                LoadingIndicatorFragment.hide()
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun loadEventData() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/events/" + idEvento + "/"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")
                var attendees =  response.getJSONArray("attendees")
                Log.i("API", "Response: $attendees")
                idInvitados = mutableListOf()
                for(i in 0 until attendees.length()) {
                    val attendee = attendees.getJSONObject(i)
                    Log.i("API", "Response: " + attendee.getString("id") + " " + attendee.getString("name"))
                    idInvitados.add(attendee.getString("id").toInt())
                }

                event_detail_title.text = response.getString("name")
                event_detail_fecha.text = response.getString("starting_datetime").substring(0,10)
                event_detail_host.text = response.getString("host")
                event_detail_number_attendees.text = attendees.length().toString() + " invitados"
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun loadFriends() {


        val amigos = ArrayList<Friend>()

        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/profiles/friends/"

        LoadingIndicatorFragment.show(this)
        val jsonArrayRequest = object: JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: $response")

                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val invited = idInvitados.indexOf(item.getInt("id")) != -1
                    if(!invited) {
                        val amigo = Friend(item.getInt("id"),
                            item.getString("first_name"),
                            item.getString("last_name"),
                            item.getString("username"),
                            item.getString("email"),
                            invited)
                        amigos.add(amigo)
                    }
                }
                showDialog(amigos)
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonArrayRequest)
    }

    private fun showDialog(amigos: ArrayList<Friend>) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_list_friends)
        dialog.show()

        dialog.add_friend_floating_add.setOnClickListener(View.OnClickListener {
            var array = adapter.getFriends()
            for (i in array.indices) {
                Log.i("API",  "Checked: " + array[i].first_name + " " + array[i].checked + " " + array[i].id)
                addPerson(idEvento, array[i].id)
            }
        })

        val recyclerView:RecyclerView = dialog.findViewById(R.id.rv_add_friends)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adapter = FriendAdapter(amigos)
        recyclerView.adapter = adapter
    }

    private fun addPerson(idEvento: Int, idFriend: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://tpp-remy.herokuapp.com/api/v1/events/" + idEvento + "/add_attendee/?attendee_id=" + idFriend

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                Log.i("API", "Response: %s".format(response.toString()))
                LoadingIndicatorFragment.hide()
            },
            Response.ErrorListener { error ->
                Log.e("API", "Error en GET")
                Log.e("API", "Response: %s".format(error.toString()))
                LoadingIndicatorFragment.hide()
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token " + token
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }
}
