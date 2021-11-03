package mr.shtein.buddyandroidclient.model

data class UserInfo(
  val token: String,
  val id: Long,
  val userName: String,
  val role: String,
  val isLocked: Boolean
)