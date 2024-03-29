@file:Suppress("unused")

package loli.ball.kemono

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import loli.ball.kemono.bean.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.internal.EMPTY_REQUEST
import okhttp3.internal.closeQuietly
import org.jsoup.Jsoup

object KemonoApi {

    val KEMONO_BASE_URLS = listOf(
        "https://kemono.su"
    )
    var KEMONO_BASE_URL = KEMONO_BASE_URLS[0]

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
        val redirect = response.header("location")
        response.close()
        if (redirect?.contains("logged_in=yes") == true) {
            val cookie1 = Cookie.parse(
                KEMONO_BASE_URL.toHttpUrl(),
                cookie.orEmpty()
            ) ?: return null
            val cook = "${cookie1.name}=${cookie1.value}"
            val time = cookie1.expiresAt
            return Account(username, password, cook, time)
        }
        return null
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
        val code = response.code
        val redirect = response.header("location")
        val bodyString = response.body?.string()
        response.close()
        return if (code == 302 && redirect?.contains("logged_in=yes") == true) {
            val cookie1 = Cookie.parse(
                KEMONO_BASE_URL.toHttpUrl(),
                cookie.orEmpty()
            ) ?: return null
            val cook = "${cookie1.name}=${cookie1.value}"
            val time = cookie1.expiresAt
            Account(username, password, cook, time)
        } else {
            if (code == 200 && bodyString != null) {
                val doc = Jsoup.parse(bodyString)
                val msg = doc.getElementsByClass("flash_messages").text()
                error(msg)
            } else null
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

    fun favoriteArtist(cookie: String, service: String, artistId: String): Boolean {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/api/v1/favorites/creator/$service/$artistId")
            .addHeader("cookie", cookie)
            .post(EMPTY_REQUEST)
            .build()
        return noRedirectsClient.newCall(request).execute().let {
            it.closeQuietly()
            it.code == 200
        }
    }

    fun unFavoriteArtist(cookie: String, service: String, artistId: String): Boolean {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/api/v1/favorites/creator/$service/$artistId")
            .addHeader("cookie", cookie)
            .delete()
            .build()
        return noRedirectsClient.newCall(request).execute().let {
            it.closeQuietly()
            it.code == 200
        }
    }

    fun favoritePost(cookie: String, service: String, artistId: String, postId: String): Boolean {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/api/v1/favorites/post/$service/$artistId/$postId")
            .addHeader("cookie", cookie)
            .post(EMPTY_REQUEST)
            .build()
        return noRedirectsClient.newCall(request).execute().let {
            it.closeQuietly()
            it.code == 200
        }
    }

    fun unFavoritePost(cookie: String, service: String, artistId: String, postId: String): Boolean {
        val request = Request.Builder()
            .url("$KEMONO_BASE_URL/api/v1/favorites/post/$service/$artistId/$postId")
            .addHeader("cookie", cookie)
            .delete()
            .build()
        return noRedirectsClient.newCall(request).execute().let {
            it.closeQuietly()
            it.code == 200
        }
    }


    //
    // 账户无关的API，不登陆也能使用
    //


    fun allArtist(cookie: String? = null, noCache: Boolean = false): Result<KemonoArtistList> {
        val url = "$KEMONO_BASE_URL/api/v1/creators.txt"
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
        val url = "$KEMONO_BASE_URL/api/v1/$service/user/$artistId?o=$offset"
        return request(client, url, cookie, noCache)
    }

    fun postDetail(
        service: String,
        artistId: String,
        postId: String,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<KemonoPost> {
        val url = "$KEMONO_BASE_URL/api/v1/$service/user/$artistId/post/$postId"
        return request(client, url, cookie, noCache)
    }

    fun postComments(
        service: String,
        artistId: String,
        postId: String,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<List<KemonoComment>> {
        return runCatching {
            val url = "$KEMONO_BASE_URL/$service/user/$artistId/post/$postId"
            val bodyString = requestGet(client, url, cookie, noCache)
            KemonoComments.parsePost(bodyString)
        }
    }

    fun artistFancards(
        service: String,
        artistId: String,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<List<KemonoFancard>> {
        return runCatching {
            val url = "$KEMONO_BASE_URL/$service/user/$artistId/fancards"
            val bodyString = requestGet(client, url, cookie, noCache)
            KemonoArtistExtends.parseFancards(bodyString)
        }
    }

    fun artistAnnouncements(
        service: String,
        artistId: String,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<List<KemonoAnnouncement>> {
        return runCatching {
            val url = "$KEMONO_BASE_URL/$service/user/$artistId/announcements"
            val bodyString = requestGet(client, url, cookie, noCache)
            KemonoArtistExtends.parseAnnouncements(bodyString)
        }
    }

    fun artistDms(
        service: String,
        artistId: String,
        cookie: String? = null,
        noCache: Boolean = false
    ): Result<List<KemonoAnnouncement>> {
        return runCatching {
            val url = "$KEMONO_BASE_URL/$service/user/$artistId/dms"
            val bodyString = requestGet(client, url, cookie, noCache)
            KemonoArtistExtends.parseAnnouncements(bodyString)
        }
    }

    private fun requestGet(
        client: OkHttpClient,
        url: String,
        cookie: String?,
        noCache: Boolean
    ): String {
        val request = Request.Builder()
            .url(url)
            .also {
                if (noCache) it.cacheControl(CacheControl.FORCE_NETWORK)
                if (cookie != null) it.addHeader("cookie", cookie)
            }
            .build()
        val response = client.newCall(request).execute()
        val bodyString = response.body!!.string()
        val code = response.code
        response.close()
        check(code == 200) { bodyString }
        return bodyString
    }

    private inline fun <reified R> request(
        client: OkHttpClient,
        url: String,
        cookie: String?,
        noCache: Boolean
    ): Result<R> {
        return runCatching {
            val bodyString = requestGet(client, url, cookie, noCache)
            json.decodeFromString(bodyString)
        }
    }

}