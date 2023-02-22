package loli.ball.kemono.bean

data class KemonoComment(
    val id: String,                 //评论id
    val user: String,               //评论发布者名称
    val message: String,            //评论内容
    val time: String,               //发布时间
)
