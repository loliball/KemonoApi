@file:Suppress("unused")

package loli.ball.kemono.bean

import kotlinx.serialization.Serializable
import loli.ball.kemono.KemonoApi.KEMONO_BASE_URL

typealias KemonoArtistList = List<KemonoArtist>

@Serializable
sealed class KemonoArtist {

    abstract val id: String
    abstract val name: String
    abstract val service: String

    val avatar by lazy { "$KEMONO_BASE_URL/icons/$service/$id" }
    val banner by lazy { "$KEMONO_BASE_URL/banners/$service/$id" }
    val kemono by lazy { "$KEMONO_BASE_URL/$service/user/$id" }
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
data class KemonoArtistData(
    override val id: String,
    override val name: String,
    override val service: String,
) : KemonoArtist()

@Serializable
data class KemonoArtistAll(
    val favorited: Int = 0,
    override val id: String,
    val indexed: Double,            //创建日期
    override val name: String,
    override val service: String,
    val updated: Double,            //更新日期
) : KemonoArtist()

@Serializable
data class KemonoArtistFavorites(
    val faved_seq: Int,
    override val id: String,
    val indexed: String,
    override val name: String,
    override val service: String,
    val updated: String
) : KemonoArtist()