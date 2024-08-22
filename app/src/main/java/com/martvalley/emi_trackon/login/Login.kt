package com.martvalley.emi_trackon.login

object Login {

    data class LoginRequest(
        val email: String,
        val password: String,
        val role: Int
    )

    data class LoginResponse(
        val access_token: String,
        val token_type: String
    )

    data class ErrorResponse(   // 401 code
        val message: String
    )
}