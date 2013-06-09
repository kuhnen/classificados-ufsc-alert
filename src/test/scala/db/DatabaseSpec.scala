package db

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import contents.Utils
import model.Item
import mail.EmailService

class DatabaseSpec extends FunSuite with BeforeAndAfter {
  var database: SQLite = _
  var items: Seq[Item] = _
  before {
    database = new SQLite("test")
    database.createTables
    items = Utils.getMonitores
  }

  after {
    database.dropTables
  }

  test("should insert Items on the DataBASE 	") {
    //items.foreach(database.insert(_))
    database.insert(items)
    items.foreach { i =>
      assert(database.exists(i))
    }
  }
  
  test("should not insert Item with same ID") {
    items.foreach(database.insert(_))
    var tableSize = database.countItems
    println("Table size -> " ,tableSize)
    intercept[java.sql.SQLException]{
      items.foreach(database.insert(_))
    }
    assert(tableSize === database.countItems)
  }

  test("should return false with not exists"){
    val item = Item(0, "23", "234","das")
    items.foreach(database.insert(_))
    assert(!database.exists(item))
      }

  test("should send email if some item does not exist"){
    
    val itemsFromWeb = Utils.getMonitores.toList
    itemsFromWeb.foreach(database.insert(_))
    val itemsFromWebPLus1 = Item(69, "teste", "teste", "caro") :: itemsFromWeb
    val itemsToSend = itemsFromWebPLus1.filterNot(database.exists(_))
    database.insert(itemsToSend)
    val message = itemsToSend.mkString("\n\n")
    EmailService.sendMessage(message)
  }
  ignore("should put all items on table") {
      val items = Utils.getAllItems
      val database = new SQLite("testAll")
      database.createTables
      database.insert(items)
    }
}