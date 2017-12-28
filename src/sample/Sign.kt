package sample

/**
 * Created by Michał on 2017-12-26.
 */
enum class Sign(val sign: kotlin.Char) {
    A('a'), B('b'), E('0');

    object static {
        fun getForChar(char: Char): Sign {
            Sign.values().forEach { enum ->
                if (enum.sign == char) {
                    return enum
                }
            }
            return E
        }
    }
}