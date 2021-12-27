package com.maltepeuniversiyesi.stok.models

data class LoginForm(
        val username: String? =null,
        val password: String? =null,
        val rememberMe: Boolean=false

)