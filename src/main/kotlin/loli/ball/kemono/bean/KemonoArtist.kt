@file:Suppress("unused")

package loli.ball.kemono.bean

import kotlinx.serialization.Serializable
import loli.ball.kemono.KemonoApi.KEMONO_BASE_URL

typealias KemonoArtistList = List<KemonoArtist>

@Serializable
sealed interface KemonoArtist {
    val id: String
    val name: String
    val service: String
}

val KemonoArtist.avatar get() = "$KEMONO_BASE_URL/icons/$service/$id"
val KemonoArtist.banner get() = "$KEMONO_BASE_URL/banners/$service/$id"
val KemonoArtist.kemono get() = "$KEMONO_BASE_URL/$service/user/$id"
val KemonoArtist.origin: String
    get() {
        val artistService = ArtistService.values().find { it.name == service } ?: return ""
        val url = artistService.url
        return when (artistService) {
            ArtistService.patreon -> "$url/user?u=$id"
            ArtistService.fanbox -> "$url/fanbox/creator/$id"
            ArtistService.gumroad -> "$url/$id"
            ArtistService.subscribestar -> "$url/$id"
            ArtistService.dlsite -> "$url/home/circle/profile/=/maker_id/$id"
            ArtistService.discord -> "$url/$id"
            ArtistService.fantia -> "$url/fanclubs/$id"
            ArtistService.boosty -> "$url/$id"
            ArtistService.afdian -> "$url/a/$id"
        }
    }

@Serializable
data class KemonoArtistAll(
    val favorited: Int = 0,
    override val id: String,
    val indexed: Double,            //创建日期
    override val name: String,
    override val service: String,
    val updated: Double,            //更新日期
) : KemonoArtist

@Serializable
data class KemonoArtistFavorites(
    val faved_seq: Int,
    override val id: String,
    val indexed: String,
    override val name: String,
    override val service: String,
    val updated: String
) : KemonoArtist