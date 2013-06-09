package contents

import scala.io.Source
import org.xml.sax.InputSource
import scala.xml.NodeSeq
import model.Item
import java.util.regex.Matcher

object Utils {
  //to config file
  val monitoresDeVideo = "Monitores de Vídeo"
  val ufscLink = "http://classificados.inf.ufsc.br/"
  val patternNumber = """\d""".r
  val patternAtLeastOneNumber = """\d+""".r

  def getSource(link: String) = {
    val source = new InputSource(link)
    val parserFactory = new org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
    val parser = parserFactory.newSAXParser()
    val adapter = new scala.xml.parsing.NoBindingFactoryAdapter
    adapter.loadXML(source, parser)
  }

  def getPrice(link: String) = {
    val source = getSource(link).mkString
    val pattern = """Preço.*\s*.*\d+""".r
    val aux = pattern.findAllIn(source)
    val price = aux flatMap (l => "\\d\\d+".r.findAllIn(l)) //)
    if (price.hasNext) price.next else "0"

  }
  def getItemsFromURL(link: String) = {
    val source = getSource(link)
    val items = (source \\ "a").map { tag =>
      val hrefString = (tag \ "@href").mkString
      val text = tag.text
      val price = if (hrefString.contains("detail")) getPrice(ufscLink + hrefString) else "0"
      val id = patternAtLeastOneNumber.findFirstIn(hrefString).getOrElse("0").toInt
      Item(id, text, ufscLink + hrefString.mkString, price)
    }
    items.filter(item => item.href.contains("detail"))

  }

  def getMonitores = getItemsFromURL(getAllCategories().get(monitoresDeVideo).head)

  def getAllItems = {
    val allURLsCat = getAllCategories
    val items = allURLsCat.flatMap { case (c, link) => getItemsFromURL(link) }
    items
  }
  def getAllCategories() = {
    val source = getSource(ufscLink)
    val categoriesToLink = (source \\ "a").map { tag =>
      val hrefString = (tag \ "@href")
      val isSubCat = !tag.text.startsWith("  ")
      val isLinkToCatId = hrefString.mkString.contains("catid")
      (tag.text.trim() -> (ufscLink + (hrefString.mkString).trim()))
    }
    categoriesToLink.filter {
      case (k, v) => (!k.matches(".*[\\d]+.*") && v.matches(".*?catid=[\\d]+"))
    }.toMap
  }
}