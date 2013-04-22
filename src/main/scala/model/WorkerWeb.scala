package model

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorRef
import contents.Utils
import db.SQLite

class WorkerWeb(link: String, master: ActorRef, db: SQLite) extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case 'getItems =>
      
      var items = Utils.getItemsFromURL(link)
      val itemss = items.filterNot(db.exists)
      master ! itemss
    case "test" ⇒
      log.info("received test")
      master ! 'done

    case _ ⇒ log.info("received unknown message")
  }

}

class Master(var worksToBeDone: Int, db: SQLite) extends Actor {
  val log = Logging(context.system, this)
  var itemsTotal = collection.mutable.LinkedList.empty[Item]

  def receive = {
    case itemsReady: List[Any] ⇒
      worksToBeDone = worksToBeDone - 1
      log.info("DOING WORK")
      
     // val itemsM = collection.mutable.LinkedList(itemsReady).asInstanceOf[collection.mutable.LinkedList[Item]]
    //  val itemsToSend = items.filterNot(db.exists)
      //db.insert(itemsToSend)
      //itemsTotal = itemsTotal ++ itemsReady.asInstanceOf[List[Item]]
    //log.info(itemsTotal.mkString)
       db.insert(itemsReady.asInstanceOf[List[Item]])
      if (worksToBeDone == 0){
       
        log.info("Done!!!!" + itemsTotal.mkString)
       
      }else
      log.info(worksToBeDone + " to GO!!!!!!!")

    case _ ⇒ log.info("received unknown message")
 
  }

}