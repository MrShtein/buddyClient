package mr.shtein.buddyandroidclient.model

data class UserInfo(
  val id: Long,
  val name: String,
  val surname: String,
  val phone: String,
  val token: String,
  val login: String,
  val role: String,
  val isLocked: Boolean
)