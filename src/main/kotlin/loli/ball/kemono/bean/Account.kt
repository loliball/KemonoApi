package loli.ball.kemono.bean

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val username: String,               //用户名
    val password: String,               //密码
    val cookie: String,                 //cookie 有效期一个月
    val expiresTimestamp: Long          //cookie过期时间戳
) {
    val isExpired = System.currentTimeMillis() > expiresTimestamp
}