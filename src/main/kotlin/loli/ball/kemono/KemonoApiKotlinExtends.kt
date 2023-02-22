package loli.ball.kemono

import loli.ball.kemono.bean.KemonoArtist
import loli.ball.kemono.bean.KemonoPost

fun KemonoArtist.favorite(cookie: String) {
    KemonoApi.favoriteArtist(cookie, service, id)
}

fun KemonoArtist.unFavorite(cookie: String) {
    KemonoApi.unFavoriteArtist(cookie, service, id)
}

fun KemonoPost.favorite(cookie: String) {
    KemonoApi.favoritePost(cookie, service, user, id)
}

fun KemonoPost.unFavorite(cookie: String) {
    KemonoApi.unFavoritePost(cookie, service, user, id)
}