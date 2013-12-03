package models

import java.util.UUID


/**
 * This base abstract class represents any entity model in the db.
 */
abstract class Entity{
  def id: UUID
  def name: String
}

/**
 * This base abstract class represents the string/input form for an entity model in the db.
 */
abstract class NewEntity{
  def name: String
}

