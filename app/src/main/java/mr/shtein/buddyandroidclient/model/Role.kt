package mr.shtein.buddyandroidclient.model

enum class Role(val num: String, val role: String) {
    USER("ROLE_USER", "Пользователь"),
    ADMIN("ROLE_ADMIN", "Администратор приюта"),
    VOLUNTEER("ROLE_VOLUNTEER","Волонтер")
}