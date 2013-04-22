package source

import org.scalatest.FunSuite
import contents.Utils

class SourceSpecSuite extends FunSuite {

  ignore("get contents from http://classificados.inf.ufsc.br/"){
  	val categories = Utils.getAllCategories
  	val linkToMonitores = categories.get("Monitores de Vídeo")
  	//println(linkToMonitores)
  }
  
  test("get all items") {
    println(Utils.getAllItems)
  }
  
  ignore("get items from monitores"){
    val items = Utils.getMonitores
    items.foreach(println)
  }
  
  ignore("get items for jacaroa") {
    val items = Utils.getItemsFromURL("http://classificados.inf.ufsc.br/index.php?catid=187")
    items foreach println
   // println (items.length)
  }
  
  ignore("get price from item source page") {
    val price = Utils.getPrice("http://classificados.inf.ufsc.br/detail.php?id=82848")
    println(price)
  }
}