package loli.ball.kemono.bean

import kotlinx.serialization.Serializable
import loli.ball.kemono.KemonoApi.KEMONO_BASE_URL

typealias KemonoPostList = List<KemonoPost>

@Serializable
data class KemonoPost(
    val added: String,                  //收藏时间
    val attachments: List<Attachment>,  //收藏的附件 可能为空
    val content: String,                //作品简介html
    val edited: String = "",            //编辑时间
//    val embed: Any? = null,           //ignore
    val faved_seq: Int = 0,             //收藏的顺序
    val file: Attachment,               //收藏的附件 封面预览图
    val id: String,                     //作品的id
    val published: String,              //发布时间
    val service: String,                //隶属于的服务器 详见ArtistService
    val shared_file: Boolean,           //未知 通常是false
    val title: String,                  //作品的标题
    val user: String,                   //作者id
)

@Serializable
data class Attachment(
    val name: String,
    val path: String    // 斜杠开头
) {
    val thumbnail: String = "$KEMONO_BASE_URL/thumbnail$path"
    val fullImage: String = "$KEMONO_BASE_URL$path"
}
