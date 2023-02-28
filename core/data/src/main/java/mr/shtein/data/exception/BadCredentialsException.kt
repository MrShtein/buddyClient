package mr.shtein.data.exception

import java.lang.Exception

class BadCredentialsException(message: String = "У Вас нет доступа к запрашиваемым данным") : Exception(message) {

}