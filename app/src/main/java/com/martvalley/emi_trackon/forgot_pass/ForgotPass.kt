package com.martvalley.emi_trackon.forgot_pass

object ForgotPass {
    data class Request(
        val email: String
    )

    data class Response(
        val message: String,
        val status: Int
    )
}