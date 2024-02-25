package ranking.store

import org.scalatest.funsuite.AnyFunSuite
import ranking.domain.{Review, StoreWithReviews}

class StoreReviewsInMemoryTest extends AnyFunSuite {
  test("state update with the same data should not change the state") {
    val initialState =
      Seq(
        StoreWithReviews(
          businessId = "1",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("1", "Review 1", "1")),
          recentReviewText = "Review 1",
          totalReviews = 1000,
          monthlyVisits = 10000
        )
      )
    val store = new StoreReviewsInMemory(initialState)

    val newData =
      Seq(
        StoreWithReviews(
          businessId = "1",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("1", "Review 1", "1")),
          recentReviewText = "Review 1",
          totalReviews = 1000,
          monthlyVisits = 10000
        )
      )

    store.updateState(newData)

    assert(store.state.length == initialState.length)
    assert(store.state.head.businessId == newData.head.businessId)
  }

  test("state update with new data should change the state") {
    val initialState =
      Seq(
        StoreWithReviews(
          businessId = "1",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("1", "Review 1", "1")),
          recentReviewText = "Review 1",
          totalReviews = 1000,
          monthlyVisits = 10000
        )
      )
    val store = new StoreReviewsInMemory(initialState)

    val newData =
      Seq(
        StoreWithReviews(
          businessId = "1",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("1", "Review 1", "1")),
          recentReviewText = "Review 1",
          totalReviews = 1000,
          monthlyVisits = 20000
        )
      )

    store.updateState(newData)

    assert(initialState.length == store.state.length)
    assert(store.state.head.businessId == newData.head.businessId)
    assert(
      store.state.head.monthlyVisits == newData.head.monthlyVisits
    )
  }

  test("state update with new store should increase the state length by 1") {
    val initialState =
      Seq(
        StoreWithReviews(
          businessId = "1",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("1", "Review 1", "1")),
          recentReviewText = "Review 1",
          totalReviews = 1000,
          monthlyVisits = 10000
        )
      )

    val store = new StoreReviewsInMemory(initialState)

    val newData =
      Seq(
        StoreWithReviews(
          businessId = "2",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("2", "Review 2", "2")),
          recentReviewText = "Review 2",
          totalReviews = 1500,
          monthlyVisits = 20000
        )
      )

    store.updateState(newData)

    assert(store.state.length == initialState.length + 1)
  }

  test("new reviews are appended for existing store") {
    val initialState =
      Seq(
        StoreWithReviews(
          businessId = "1",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("1", "Review 1", "1")),
          recentReviewText = "Review 1",
          totalReviews = 1000,
          monthlyVisits = 10000
        )
      )

    val store = new StoreReviewsInMemory(initialState)

    val newData =
      Seq(
        StoreWithReviews(
          businessId = "1",
          name = "Store",
          category = "Fancy Store",
          reviews = Seq(Review("2", "Review 2", "1")),
          recentReviewText = "Review 2",
          totalReviews = 1001,
          monthlyVisits = 10005
        )
      )

    store.updateState(newData)

    assert(store.state.length == initialState.length)
    assert(store.state.head.reviews.length == 2)
    assert(store.state.head.totalReviews == 1001)
    assert(store.state.head.monthlyVisits == 10005)
  }
}
