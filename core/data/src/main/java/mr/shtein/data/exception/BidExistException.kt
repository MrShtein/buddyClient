package mr.shtein.data.exception

import java.lang.Exception

class BidExistException(message: String = "Ваша заявка уже обрабатывается") : Exception(message) {

}