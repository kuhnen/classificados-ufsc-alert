package db

import scala.slick.session.Database
import scala.slick.jdbc.StaticQuery
import scala.slick.jdbc.meta.MTable
import Database.threadLocalSession
import scala.slick.driver.SQLiteDriver.simple._

class SQLite(name: String) {

  val database = Database.forURL(
    "jdbc:sqlite:%s.db" format name,
    driver = "org.sqlite.JDBC")	

  def exists(item: model.Item): Boolean = database withSession {
    Query(Items.where(_.id === item.id).exists).first
  }

  def countItems: Int = database  withSession {
    Query(Items.length).first
  }
  
  def insert(items: Iterable[model.Item]): Unit = database withSession {
    items.foreach(i => Items.insert(i.id, i.description, i.href, i.price))
  }
  
  def insert(item: model.Item): Int = database withSession {
    Items.insert(item.id, item.description, item.href, item.price)
  }
  def createTables = database withSession (Items.ddl.create)

  def dropTables = database withSession (Items.ddl.drop)

  def tableNames(): Set[String] = database withSession {
    (MTable.getTables.list() map { _.name.name }).toSet
  }

  def insertItem(id: String, description: String, price: String) {
    database("insert into test_table values ('%s', '%s', '%s')".format(id, description, price))
  }
  def WAL() = database withSession {
    StaticQuery.updateNA("PRAGMA journal_mode=WAL").execute
    
  }
  
  implicit class DatabaseOps(database: Database) {
    def apply(sql: String) {
      database withSession {
               
        StaticQuery.updateNA(sql).execute
      }
    }
  }
}

