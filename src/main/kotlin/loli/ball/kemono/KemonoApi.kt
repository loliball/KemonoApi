@file:Suppress("unused")

package loli.ball.kemono

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import loli.ball.kemono.bean.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.internal.EMPTY_REQUEST

object KemonoApi {

    const val KEMONO_BASE_URL = "https://kemono.party"

    var client: OkHttpClient = OkHttpClient()

    var noRedirectsClient = OkHttpClient.Builder()
        .followRedirects(false)
        .build()

    private var json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun login(username: String, password: String): Account? {
        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/account/login")
            .post(body)
            .build()
        val response = noRedirectsClient.newCall(request).execute()
        val cookie = response.header("set-cookie")
        return when (response.header("location")) {
            "/artists?logged_in=yes" -> {
                val cookie1 = Cookie.parse(
                    KEMONO_BASE_URL.toHttpUrl(),
                    cookie.orEmpty()
                ) ?: return null
                val cook = "${cookie1.name}=${cookie1.value}"
                val time = cookie1.expiresAt
                Account(username, password, cook, time)
            }

            "/account/login" -> null
            else -> null
        }
    }

    fun register(username: String, password: String): Account? {
        val body = FormBody.Builder()
            .add("favorites", "")
            .add("username", username)
            .add("password", password)
            .add("confirm_password", password)
            .build()
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/account/register")
            .post(body)
            .build()
        val response = noRedirectsClient.newCall(request).execute()
        val cookie = response.header("set-cookie")
        return if (response.code == 302 &&
            response.header("location") == "/artists?logged_in=yes"
        ) {
            val cookie1 = Cookie.parse(
                KEMONO_BASE_URL.toHttpUrl(),
                cookie.orEmpty()
            ) ?: return null
            val cook = "${cookie1.name}=${cookie1.value}"
            val time = cookie1.expiresAt
            Account(username, password, cook, time)
        } else {
            null
        }
    }

    fun favoriteArtists(cookie: String, noCache: Boolean = false): Result<KemonoArtistList> {
        val url = "$KEMONO_BASE_URL/api/v1/account/favorites?type=artist"
        return request<List<KemonoArtistFavorites>>(client, url, cookie, noCache)
    }

    fun favoritePosts(cookie: String, noCache: Boolean = false): Result<KemonoPostList> {
        val url = "$KEMONO_BASE_URL/api/v1/account/favorites?type=post"
        return request(client, url, cookie, noCache)
    }

    fun favoriteArtist(cookie: String, service: String, artistId: String) {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/favorites/artist/$service/$artistId")
            .addHeader("cookie", cookie)
            .post(EMPTY_REQUEST)
            .build()
        noRedirectsClient.newCall(request).execute()
    }

    fun unFavoriteArtist(cookie: String, service: String, artistId: String) {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/favorites/artist/$service/$artistId")
            .addHeader("cookie", cookie)
            .delete()
            .build()
        noRedirectsClient.newCall(request).execute()
    }

    fun favoritePost(cookie: String, service: String, artistId: String, postId: String) {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/favorites/post/$service/$artistId/$postId")
            .addHeader("cookie", cookie)
            .post(EMPTY_REQUEST)
            .build()
        noRedirectsClient.newCall(request).execute()
    }

    fun unFavoritePost(cookie: String, service: String, artistId: String, postId: String) {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/favorites/post/$service/$artistId/$postId")
            .addHeader("cookie", cookie)
            .delete()
            .build()
        noRedirectsClient.newCall(request).execute()
    }


    //
    // 账户无关的API，不登陆也能使用
    //


    fun allArtist(cookie: String? = null, noCache: Boolean = false): Result<KemonoArtistList> {
        val url = "$KEMONO_BASE_URL/api/creators"
        return request<List<KemonoArtistAll>>(client, url, cookie, noCache)
    }

//    fun allPosts(
//        offset: Int = 0,
//        search: String? = null,
//        cookie: String? = null,
//        noCache: Boolean = false
//    ): Result<KemonoPostList> {
//        val url = "$KEMONO_BASE_URL/posts?o=$offset" + if (search.isNullOrEmpty()) "" else "&q=$search"
//        return request(client, url, cookie, noCache)
//    }

    fun artistDetail(
        service: String,
        artistId: String,
        offset: Int = 0,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<KemonoPostList> {
        val url = "$KEMONO_BASE_URL/api/$service/user/$artistId?o=$offset"
        return request(client, url, cookie, noCache)
    }

    fun postDetail(
        service: String,
        artistId: String,
        postId: String,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<KemonoPostList> {
        val url = "$KEMONO_BASE_URL/api/$service/user/$artistId/post/$postId"
        return request(client, url, cookie, noCache)
    }

    fun postComments(
        service: String,
        artistId: String,
        postId: String,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<List<KemonoComment>> {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/$service/user/$artistId/post/$postId")
            .also {
                if (noCache) it.cacheControl(CacheControl.FORCE_NETWORK)
                if (cookie != null) it.addHeader("cookie", cookie)
            }
            .build()
        return runCatching {
            val response = client.newCall(request).execute()
            val bodyString = response.body!!.string()
            check(response.code == 200) { bodyString }
            KemonoComments.parsePost(bodyString)
        }
    }

    private inline fun <reified R> request(
        client: OkHttpClient,
        url: String,
        cookie: String?,
        noCache: Boolean
    ): Result<R> {
        val request = Request.Builder()
            .url(url)
            .also {
                if (noCache) it.cacheControl(CacheControl.FORCE_NETWORK)
                if (cookie != null) it.addHeader("cookie", cookie)
            }
            .build()
        return runCatching {
            val response = client.newCall(request).execute()
            val bodyString = response.body!!.string()
            check(response.code == 200) { bodyString }
            json.decodeFromString(bodyString)
        }
    }

}