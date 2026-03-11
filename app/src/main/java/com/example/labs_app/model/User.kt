package com.example.labs_app.model

import java.io.Serializable

/**
 * Модель пользователя для передачи между Activity (Serializable).
 */
data class User(
    val username: String,
    val email: String,
    val password: String
) : Serializable
