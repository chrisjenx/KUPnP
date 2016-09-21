package kupnp

/**
 * Created by chris on 16/04/2016.
 * For project kupnp
 */

var LOGGING: Boolean = true

fun debug(any: Any?) {
    if (LOGGING) System.out.println(any)
}

fun warn(any: Any?) {
    if (LOGGING) System.out.println(any)
}

fun log(any: Any?) {
    if (LOGGING) System.out.println(any)
}