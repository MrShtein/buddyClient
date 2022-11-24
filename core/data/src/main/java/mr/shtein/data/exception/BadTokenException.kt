package mr.shtein.data.exception

import java.lang.Exception

class BadTokenException(message: String = "Токен устарел") : Exception(message) {

}