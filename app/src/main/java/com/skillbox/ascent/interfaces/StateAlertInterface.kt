package com.skillbox.ascent.interfaces

//передаем с помощью этого интерфейса состояние напоминаний в MainActivity.Запускаю и слушаю в
//MainActivity, потому что по умолчанию напоминания должны работать, если даже пользователь не
// открывал фрагмент с настройками напоминания
interface StateAlertInterface {
    fun isStartWM(isStart: Boolean)
}