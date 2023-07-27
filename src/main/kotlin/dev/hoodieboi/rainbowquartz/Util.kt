package dev.hoodieboi.rainbowquartz

/**
 * @return The left value if the condition is true, else null is returned
 */
infix fun <T> T?.onlyIf(condition: Boolean): T? {
    return if (condition) this
    else null
}

infix fun <T> T?.onlyIf(condition: (T) -> Boolean): T? {
    this ?: return null
    return this.onlyIf(condition(this))
}