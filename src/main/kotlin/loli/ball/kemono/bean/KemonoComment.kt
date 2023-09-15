package loli.ball.kemono.bean

import kotlinx.serialization.Serializable

@Serializable
data class KemonoComment(
    val id: String,                 //评论id
    val user: String,               //评论发布者名称
    val message: String,            //评论内容
    val time: String,               //发布时间
)

@Serializable
data class KemonoFancard(
    val time: String,               //时间 年-月
    val attachment: Attachment      //图片
)

@Serializable
data class KemonoAnnouncement(
    val time: String,               //时间 年-月
    val text: String                //内容
)