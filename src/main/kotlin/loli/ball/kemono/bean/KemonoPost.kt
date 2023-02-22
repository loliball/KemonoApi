package loli.ball.kemono.bean

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
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
    @Serializable(with = AttachmentSerializer::class)
    val file: Attachment?,              //收藏的附件 封面预览图
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

object AttachmentSerializer : KSerializer<Attachment?> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AttachmentSerializer") {
            element<String>("name")
            element<String>("path")
        }

    override fun serialize(encoder: Encoder, value: Attachment?) {
        if (value != null) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.name)
                encodeStringElement(descriptor, 1, value.path)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Attachment? =
        decoder.decodeStructure(descriptor) {
            var name: String? = null
            var path: String? = null
            if (decodeSequentially()) {
                name = decodeStringElement(descriptor, 0)
                path = decodeStringElement(descriptor, 1)
            } else while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, 0)
                    1 -> path = decodeStringElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            if (name == null || path == null) null
            else Attachment(name, path)
        }

}