package ranking.store

import ranking.domain.StoreWithReviews

class StoreReviewsInMemory(initialState: Seq[StoreWithReviews]) {
  var state: Seq[StoreWithReviews] = initialState

  def updateState(
      storeWithReviewsSeq: Seq[StoreWithReviews]
  ): Seq[StoreWithReviews] = {

    val newState = storeWithReviewsSeq.foldLeft(state) {
      (acc, storeWithReviews) =>
        val index = acc.indexWhere(_.businessId == storeWithReviews.businessId)
        index match {
          case -1 => acc :+ storeWithReviews
          case _ =>
            val oldStore = acc(index)
            val newReviews = storeWithReviews.reviews.filterNot(review =>
              oldStore.reviews.exists(_.id == review.id)
            )
            val updatedReviews = newReviews ++ oldStore.reviews

            val updatedStore = {
              storeWithReviews.copy(reviews = updatedReviews)
            }

            acc.updated(index, updatedStore)
        }
    }

    state = newState
    newState
  }
}
