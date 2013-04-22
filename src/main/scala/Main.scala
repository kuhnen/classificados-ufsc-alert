import db.SQLite
import scala.slick.session.Database
import Database.threadLocalSession
import db.SQLite
import model.Item
import akka.actor.Actor
import akka.actor.Props
import scala.concurrent.duration._
import akka.actor.ActorSystem._
import scalaz.concurrent.Actors
import akka.actor.ActorSystem
import contents.Utils
import mail.EmailService
//Use the system's dispatcher as ExecutionContext

object WatcherInfClassificados {
  val database = new SQLite("testAll")
 
  def main(args: Array[String]) {
   
    if (!database.tableNames().contains("Item"))
      database.createTables

    val system = ActorSystem("MySystem")
    import system.dispatcher
    system.scheduler.schedule(0 second, 30 minute)(watch)
  }

  def watch {

    val itemsFromWeb = Utils.getAllItems
    val itemsToSend = itemsFromWeb.filterNot(database.exists(_))
    if (itemsToSend.isEmpty)
      println("Nothing new")
    else {
      database.insert(itemsToSend)
      val message = itemsToSend.mkString("\n\n")
      println("new stuff on site: " + message)
      EmailService.sendMessage(message)
    }
  }

}