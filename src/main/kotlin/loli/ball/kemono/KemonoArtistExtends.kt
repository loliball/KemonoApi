package loli.ball.kemono

import loli.ball.kemono.bean.Attachment
import loli.ball.kemono.bean.KemonoAnnouncement
import loli.ball.kemono.bean.KemonoFancard
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object KemonoArtistExtends {

    fun parseFancards(html: String):List<KemonoFancard> {
        val doc = Jsoup.parse(html)
        doc.outputSettings(Document.OutputSettings().prettyPrint(false))

        val output = mutableListOf<KemonoFancard>()
        val group = doc.getElementById("fancard-container")
        group?.children()?.forEach {
            val time = it.getElementsByTag("span").first()!!.attr("title")
            val imageUrl = it.getElementsByTag("img").first()!!.attr("src")
            val imageHash = imageUrl.substringAfter("/data")
            val imageName = imageUrl.substringAfterLast('/')
            output += KemonoFancard(time, Attachment(imageName, imageHash))
        }
        return output
    }

    fun parseAnnouncements(html: String) : List<KemonoAnnouncement> {
        val doc = Jsoup.parse(html)
        doc.outputSettings(Document.OutputSettings().prettyPrint(false))

        val output = mutableListOf<KemonoAnnouncement>()
        doc.getElementsByClass("dm-card").forEach {
            val text = it.getElementsByClass("dm-card__body").text()
            val time = it.getElementsByClass("dm-card__footer").text()
            output += KemonoAnnouncement(time.replace("Published:", "").trim(), text)
        }
        return output
    }

}