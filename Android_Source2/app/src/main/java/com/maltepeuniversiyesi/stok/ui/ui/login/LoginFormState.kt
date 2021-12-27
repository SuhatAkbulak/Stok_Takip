package com.maltepeuniversiyesi.stok.ui.ui.login

/**
 * com.maltepeuniversiyesi.stok.models.Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)