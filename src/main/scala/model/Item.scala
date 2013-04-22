package model

case class Item(id: Int, description: String, href: String, price: String) {
  override def toString = "id: "+id + "\t " + description + "\n\tPrice: " + price + "\n\t Link: " + href
}