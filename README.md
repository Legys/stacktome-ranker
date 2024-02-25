### Stacktome ranker Scala Spark App

## Tech assignment

The task is to create a domain ranker data processing service that
would pull recently reviewed domains from trustpilot.com from categories,
filter by ‘store’ in the category & match with traffic data from vstat.info

For each domain there will be enough
* a domain,
* latest review (only 1, if updated should use latest),
* latest review count (since service has started),
* total review count, traffic.
* It’s enough to show top 10 domains.

The service should update the data at least every 5 min.

Ranking should be done by recent review count (the more reviews the higher) & traffic count (the more traffic the higher).
If recent review count is the same, the domain with higher traffic should be shown higher.


## How to run
1. Clone the repository
2. Run `docker build -t stacktome-ranker .` in the root of the project.
3. Start the `docker run -e FETCH_INTERVAL_MINUTES=1 -e COOKIE_SESSION_HEADER=vstat_session=yourToken stacktome-ranker` or use Docker Desktop to run the image.
4. The app will start and fetch the data from the sources. The data will be updated every n (1 by default) minutes.
   You should see the table with the top 10 domains in the console. Example: https://postimg.cc/HcB6H6pL
