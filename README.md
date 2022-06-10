## Search Engine

### Indexer service

**Other services:**

- [**`crawler`**](https://github.com/Wildcall/search_engine/tree/master/crawler) 
- [**`indexer`**](https://github.com/Wildcall/search_engine/tree/master/indexer) <
- [**`searcher`**](https://github.com/Wildcall/search_engine/tree/master/searcher)
- [**`task`**](https://github.com/Wildcall/search_engine/tree/master/task_manager)
- [**`notification`**](https://github.com/Wildcall/search_engine/tree/master/notification)
- [**`registration`**](https://github.com/Wildcall/search_engine/tree/master/registration)

**Build:**

```
cd path_to_project
docker-compose up
mvn clean package repack
```

**Running:**
```
java -jar -DDATABASE_URL=postgresql://localhost:5431/se_indexer_data -DDATABASE_USER=indexer_user -DDATABASE_PASS=indexer_password -DCRAWLER_SECRET=INDEXER_SECRET -DTASK_MANAGER_SECRET=TASK_MANAGER_SECRET -DCRAWLER_SECRET=CRAWLER_SECRET
```

**Environment Variable:**

- `DATABASE_URL` postgresql://localhost:5431/se_indexer_data
- `DATABASE_USER` indexer_user
- `DATABASE_PASS` indexer_password
- `INDEXER_SECRET`
- `TASK_MANAGER_SECRET`
- `CRAWLER_SECRET`

**Api:**

- api/v1
- api/v1/indexer/start
- api/v1/indexer/stop
- api/v1/index?siteId={siteId}&appUserId={appUserId}
- api/v1/lemma?siteId={siteId}&appUserId={appUserId}
- api/v1/sse/indexer