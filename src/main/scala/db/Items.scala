package db

import scala.slick.driver.SQLiteDriver.simple._

object Items extends Table[(Int, String, String, String)]("Item"){
  
  def id = column[Int]("ITEM_ID", O.AutoInc) // This is the primary key column
  def description = column[String]("DESCRIPTION")
  def link = column[String]("LINK")
  def price = column[String]("PRICE")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id ~ description ~ link ~ price
 
 
}


 