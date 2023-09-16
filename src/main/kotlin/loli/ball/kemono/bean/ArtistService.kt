package loli.ball.kemono.bean

@Suppress("UNUSED")
enum class ArtistService(
    val url: String,
    val announcement: Boolean = false,
    val fancard: Boolean = false,
    val dms: Boolean = false
) {
    patreon("https://www.patreon.com", announcement = true, dms = true),
    fanbox("https://www.pixiv.net", announcement = true, fancard = true),
    gumroad("https://gumroad.com"),
    subscribestar("https://www.subscribestar.com"),
    dlsite("https://www.dlsite.com"),
    discord("https://discord.com"),
    fantia("https://fantia.jp"),
    boosty("https://boosty.to"),
    afdian("https://afdian.net")
}
