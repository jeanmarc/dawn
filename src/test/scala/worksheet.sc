

class Content(val name: String, val value: String) {
  override def equals(o: Any): Boolean = {
    println(s"equals called on ${this} for ${o}")
    o match {
      case o: Content => (this.name == o.name) && (this.value == o.value)
      case _ => false
    }
  }

  override def toString(): String = s"name[$name]value[$value]"
}

val a = Set(new Content("a", "aaaaa"), new Content("b", "bbbbbb"))

val b = Set(new Content("a", "AAAAA"), new Content("b", "bbbbbb"))
val c = Set(new Content("a", "aaaaa"), new Content("b", "bbbbbb"))

(a.size == b.size) && (a forall (b.contains))

(a.size == c.size) && (a forall (c.contains))
