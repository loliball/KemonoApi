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