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
import model.Master
import model.WorkerWeb
//Use the system's dispatcher as ExecutionContext

object WatcherInfClassificados {
  val database = new SQLite("testAll")
 
  def main(args: Array[String]) {
   
    if (!database.tableNames().contains("Item"))
      database.createTables

    val system = ActorSystem("MySystem")
    val categories = Utils.getAllCategories
	val nWorkers = categories.size
	val db = new SQLite("actor")
	val master = system.actorOf(Props(new Master(nWorkers, db)))
	//println(db.WAL)
	if (!db.tableNames.contains("Item"))	
		db.createTables
	val workers = categories.map{ case (k, v) =>
	system.actorOf(Props(new WorkerWeb(v,master, db)))
	}
	workers.foreach(_ ! 'getItems)
	
    
    
    
   // import system.dispatcher
   // system.scheduler.schedule(0 second, 30 minute)(watch)
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