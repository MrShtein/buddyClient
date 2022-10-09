package mr.shtein.buddyandroidclient.exceptions.validate

import java.lang.Exception

class BadTokenException(message: String = "Токен устарел") : Exception(message) {

}