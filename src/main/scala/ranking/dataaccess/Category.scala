package ranking.dataaccess

sealed trait Category {
  val name: String
}

case class JewelryStore() extends Category {
  val name = "jewelry_store"
}

case class ClothingStore() extends Category {
  val name = "clothing_store"
}

case class FurnitureStore() extends Category {
  val name = "furniture_store"
}
