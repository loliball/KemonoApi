@file:Suppress("unused")

package loli.ball.kemono.bean

import kotlinx.serialization.Serializable
import loli.ball.kemono.KemonoApi.KEMONO_BASE_URL

typealias KemonoArtistList = List<KemonoArtist>

@Serializable
data class KemonoArtist(
    val id: String,
    val name: String,
    val service: String
) {
    val avatar = "$KEMONO_BASE_URL/icons/$service/$id"
    val banner = "$KEMONO_BASE_URL/banners/$service/$id"
    val kemono = "$KEMONO_BASE_URL/$service/user/$id"
    val origin by lazy {
        val artistService = ArtistService.values().find { it.name == service } ?: return@lazy ""
        val url = artistService.url
        when (artistService) {
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
}

@Serializable
data class KemonoArtistAll(
    val favorited: Int = 0,
    val id: String,
    val indexed: Double,            //创建日期
    val name: String,
    val service: String,
    val updated: Double,            //更新日期
)

@Serializable
data class KemonoArtistFavorites(
    val faved_seq: Int,
    val id: String,
    val indexed: String,
    val name: String,
    val service: String,
    val updated: String
)