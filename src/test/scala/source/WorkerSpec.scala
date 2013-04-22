package source

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import akka.actor.Props
import model.WorkerWeb
import contents.Utils
import model.WorkerWeb
import model.Master
import db.SQLite


class WorkerSpec extends FunSuite {
  
  test("Creating worker test") {
	val system = ActorSystem("MySystem")
	val categories = Utils.getAllCategories
	val nWorkers = categories.size
	val db = new SQLite("actor")
	val master = system.actorOf(Props(new Master(nWorkers, db)))
	println(db.WAL)
	if (!db.tableNames.contains("Item"))	
		db.createTables
	val workers = categories.map{ case (k, v) =>
	system.actorOf(Props(new WorkerWeb(v,master, db)))
	}
	workers.foreach(_ ! 'getItems)
	
	
  }

}