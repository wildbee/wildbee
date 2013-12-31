package observers

import models.traits.Observer
import models.traits.Observable


case class Bugzilla extends Observer {
  def update(s: Observable){
    println(s"I $name have observed a change in the '$s'")
  }
}

