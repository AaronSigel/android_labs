package com.example.labs_app

/**
 * Ключи для Intent extras (передача данных между Activity).
 */
object IntentKeys {
    /** Объект пользователя (Serializable). */
    const val USER = "user"

    /** Строковые extras — демонстрация передачи через primitive/String. */
    const val USERNAME = "username"
    const val EMAIL = "email"
    const val PASSWORD = "password"
}
