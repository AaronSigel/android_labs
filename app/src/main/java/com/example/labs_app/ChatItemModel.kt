package com.example.labs_app

/**
 * Модель элемента списка чатов для HomeActivity.
 * @param id уникальный идентификатор
 * @param senderName имя отправителя
 * @param lastMessage последнее сообщение
 * @param timestamp время (отображаемая строка)
 */
data class ChatItemModel(
    val id: String,
    val senderName: String,
    val lastMessage: String,
    val timestamp: String
)

/** Мок-набор чатов (≥8 элементов) для экрана Home. */
fun getMockChatList(): List<ChatItemModel> = listOf(
    ChatItemModel("1", "Алексей", "Привет! Как дела?", "10:30"),
    ChatItemModel("2", "Мария", "Документы отправила", "09:15"),
    ChatItemModel("3", "Иван", "Созвон в 14:00?", "Вчера"),
    ChatItemModel("4", "Елена", "Спасибо за помощь", "Вчера"),
    ChatItemModel("5", "Дмитрий", "Готово к ревью", "Пн"),
    ChatItemModel("6", "Ольга", "Напоминаю про встречу", "Пн"),
    ChatItemModel("7", "Сергей", "Ок, принято", "Вс"),
    ChatItemModel("8", "Анна", "Когда будет отчёт?", "Вс")
)
