package com.gideonst.linkedinlogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.linkedin.platform.AccessToken
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import com.linkedin.platform.utils.Scope.W_SHARE
import com.linkedin.platform.utils.Scope.R_BASICPROFILE
import com.linkedin.platform.APIHelper
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.ApiListener


class MainActivity : AppCompatActivity() {


    var url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)"

    lateinit var apiHelper: APIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiHelper = APIHelper.getInstance(applicationContext)


        LISessionManager.getInstance(applicationContext).init(this, buildScope(), object : AuthListener {
            override fun onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
            }

            override fun onAuthError(error: LIAuthError) {
                // Handle authentication errors
            }
        }, true)


        apiHelper.getRequest(this, url, object : ApiListener {
            override fun onApiSuccess(apiResponse: ApiResponse) {
                // Success!

                Log.d("MAIN ACTIVITY", apiResponse.toString())
            }

            override fun onApiError(liApiError: LIApiError) {
                // Error making GET request!
            }
        })
    }

    // Build the list of member permissions our LinkedIn session requires
    private fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

    }
}
