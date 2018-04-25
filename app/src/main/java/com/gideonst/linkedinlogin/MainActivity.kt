package com.gideonst.linkedinlogin

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import com.linkedin.platform.APIHelper
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.ApiListener
import android.util.Base64
import org.json.JSONException
import java.security.MessageDigest




class MainActivity : AppCompatActivity() {


    lateinit var apiHelper: APIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiHelper = APIHelper.getInstance(applicationContext)

        computePakageHash()


        LISessionManager.getInstance(applicationContext).init(this, buildScope(), object : AuthListener {
            override fun onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                getUsersData()
            }

            override fun onAuthError(error: LIAuthError) {
                // Handle authentication errors
                Log.e("ERORR", error.toString())
            }
        }, true)


    }


    private fun getUsersData() {
     /*   val url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))"*/
        val url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address)"

        apiHelper.getRequest(this, url, object : ApiListener {
            override fun onApiSuccess(apiResponse: ApiResponse) {
                // Success!
                Log.d("MAIN ACTIVITY", apiResponse.toString())
                try {
                    val jsonObject = apiResponse.responseDataAsJson
                    val firstName = jsonObject.getString("firstName")
                    val lastName = jsonObject.getString("lastName")
                    val emailAddress = jsonObject.getString("emailAddress")

                    Log.d("TEST",firstName)



                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onApiError(liApiError: LIApiError) {
                // Error making GET request!
                Log.e("ERRORAPI", liApiError.toString())
            }
        })
    }

    // Build the list of member permissions our LinkedIn session requires
    private fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

    }

    private fun computePakageHash() {
        try {
            val info = packageManager.getPackageInfo(
                    "com.gideonst.linkedinlogin",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: Exception) {
            Log.e("TAG", e.message)
        }

    }
}
