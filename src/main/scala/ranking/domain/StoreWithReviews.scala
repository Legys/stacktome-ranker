package ranking.domain

case class Review(
    id: String,
    text: String,
    businessId: String
)
case class StoreWithReviews(
    businessId: String,
    category: String,
    name: String,
    reviews: Seq[Review],
    recentReviewText: String,
    totalReviews: Int,
    monthlyVisits: Int
)
