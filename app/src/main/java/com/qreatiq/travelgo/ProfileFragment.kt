package com.qreatiq.travelgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import android.R.id.edit
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginManager


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var confirm_password: EditText? = null
    private var name: EditText? = null
    private var phone: EditText? = null
    private var tour_name: EditText? = null
    private var tour_description: EditText? = null
    private var save: Button? = null
    private var create_package: Button? = null
    private var layout: ConstraintLayout? = null
    private var queue: RequestQueue? = null
    private var user: SharedPreferences? = null
    private var userID: String? = null
    private var logout: Button? = null
    private var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        user = activity!!.getSharedPreferences("user_id", Context.MODE_PRIVATE)
        userID = user!!.getString("user_id", "Data Not Found")

        FacebookSdk.sdkInitialize(getApplicationContext());

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = view.findViewById(R.id.name) as EditText
        email = view.findViewById(R.id.email) as EditText
        password = view.findViewById(R.id.password) as EditText
        confirm_password = view.findViewById(R.id.confirm_password) as EditText
        phone = view.findViewById(R.id.phone) as EditText
        tour_name = view.findViewById(R.id.tour_name) as EditText
        tour_description = view.findViewById(R.id.tour_description) as EditText
        save = view.findViewById(R.id.button4) as Button
        create_package = view.findViewById(R.id.create_package) as Button
        layout = view.findViewById(R.id.frameLayout3) as ConstraintLayout
        queue = Volley.newRequestQueue(activity)

        getData()

        save!!.setOnClickListener(View.OnClickListener {
            saveData()
            name!!.setFocusable(false);
            email!!.setFocusable(false);
            password!!.setFocusable(false);
            confirm_password!!.setFocusable(false);
            phone!!.setFocusable(false);
            tour_name!!.setFocusable(false);
            tour_description!!.setFocusable(false);
        })

        create_package!!.setOnClickListener(View.OnClickListener {
            val `in` = Intent(activity, PackageActivity::class.java)
            startActivity(`in`)
        })

        logout = view!!.findViewById(R.id.logoutBtn) as Button

        logout!!.setOnClickListener{

            user = activity!!.getSharedPreferences("user_id", Context.MODE_PRIVATE)
            editor = user!!.edit()
            editor!!.clear().apply()

            FacebookSdk.sdkInitialize(activity)
            LoginManager.getInstance().logOut()

            val intent = Intent(activity, LoginMenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }

    fun getData(){
        val url = "https://3gomedia.com/travel-go/api/profile.php"

        val jsonObject = JSONObject()
        jsonObject.put("id",userID)

        val jsonObjectRequest = object :
            JsonObjectRequest(Request.Method.POST, url, jsonObject,
                Response.Listener { response ->

                    name!!.setText(response.getJSONObject("user").getString("name"))
                    email!!.setText(response.getJSONObject("user").getString("email"))
                    phone!!.setText(response.getJSONObject("user").getString("phone"))
                    if(!response.getJSONObject("user").isNull("name_tour"))
                        tour_name!!.setText(response.getJSONObject("user").getString("name_tour"))
                    if(!response.getJSONObject("user").isNull("description_tour"))
                        tour_description!!.setText(response.getJSONObject("user").getString("description_tour"))
                },
                Response.ErrorListener { error -> Log.e("error", error.message) }
            ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue!!.add(jsonObjectRequest)
    }


    fun saveData(){
        val url = "https://3gomedia.com/travel-go/api/saveProfile.php"

        val json = JSONObject()
        json.put("name",name!!.text.toString())
        json.put("email",email!!.text.toString())
        json.put("password",password!!.text.toString())
        json.put("phone",phone!!.text.toString())
        json.put("tour_name",tour_name!!.text.toString())
        json.put("tour_description",tour_description!!.text.toString())
        json.put("id",userID)

        val jsonObjectRequest = object :
            JsonObjectRequest(Request.Method.POST, url, json,
                Response.Listener { response ->
                    val snackbar = Snackbar.make(layout!!,"Profile Updated",Snackbar.LENGTH_LONG)
                    snackbar.show()
                },
                Response.ErrorListener { error -> Log.e("error", error.message) }
            ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue!!.add(jsonObjectRequest)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
