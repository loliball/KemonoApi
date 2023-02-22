package loli.ball.kemono

import loli.ball.kemono.bean.KemonoComment
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object KemonoComments {

    fun parsePost(html: String): List<KemonoComment> {
        val doc = Jsoup.parse(html)
        doc.outputSettings(Document.OutputSettings().prettyPrint(false))

        val comments = mutableListOf<KemonoComment>()
        val comm = doc.select("#page > footer > div.post__comments").ifEmpty { null }?.get(0)
        for (com in comm?.children().orEmpty()) {
            if (com.tag().name == "article" && com.hasClass("comment")) {
                val id1 = com.attr("id")
                val user1 = com.select("header").text()
                val message1 = com.select("section").text()
                val time1 = com.select("footer > time").attr("datetime")
                comments += KemonoComment(id1, user1, message1, time1)
            }
        }
        return comments
    }

}