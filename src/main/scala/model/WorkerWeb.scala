package model

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorRef
import contents.Utils
import db.SQLite
import mail.EmailService
import scala.collection.mutable.LinkedList

class WorkerWeb(link: String, master: ActorRef, db: SQLite) extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case 'getItems =>
      val items = Utils.getItemsFromURL(link)
      val newItems = items.filterNot(db.exists)
      master ! newItems
    case _ ⇒ log.info("received unknown message")
  }

}

class Master(var worksToBeDone: Int, db: SQLite) extends Actor {
  val log = Logging(context.system, this)
  var itemsTotal = List.empty[Item] //collection.mutable.LinkedList.empty[Item]

  def receive = {
    case itemsReady: List[Any] ⇒
      worksToBeDone = worksToBeDone - 1
      itemsTotal = itemsTotal ::: itemsReady.asInstanceOf[List[Item]]
      db.insert(itemsReady.asInstanceOf[List[Item]])
      //log.info(worksToBeDone + " to GO!!!!!!!")
      if (worksToBeDone == 0) {
        if (!itemsTotal.isEmpty){
        val message = itemsTotal.mkString("\n")
        EmailService.sendMessage(message)
        println(message)
        }
        
        context.system.shutdown
      }
    case _ ⇒ log.info("received unknown message")

  }

}