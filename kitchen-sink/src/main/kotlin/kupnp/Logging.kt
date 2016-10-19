package kupnp

/**
 * Created by chris on 16/04/2016.
 * For project kupnp
 */

object Logging {
    var LOGGING: Boolean = true
    var debugLog: (message: Any?) -> Unit = { System.out.println(it) }
    var warnLog: (message: Any?) -> Unit = { System.out.println(it) }
    var infoLog: (message: Any?) -> Unit = { System.out.println(it) }
}

fun debug(any: Any?) {
    if (Logging.LOGGING) Logging.debugLog(any)
}

fun info(any: Any?) {
    if (Logging.LOGGING) Logging.infoLog(any)
}

fun warn(any: Any?) {
    if (Logging.LOGGING) Logging.warnLog(any)
}
