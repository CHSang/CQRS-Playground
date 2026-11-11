# CQRS Playground

CQRS (Command Query Responsibility Segregation) step by step, with a tiny bank
account example. Every level has the same API: one command and one query.

| Level | Module | Port | Idea |
|---|---|---|---|
| 1 | `level-1-split-code` | 8081 | One database, one table. Command code and query code are split. |
| 2 | `level-2-separate-tables` | 8082 | One database. A write table and a separate read table. |
| 3 | `level-3-separate-database` | 8083 | A write database and a read database, synced through RabbitMQ. |

API (same for every level, only the port changes):

- Command: `POST /api/accounts` with `{"owner": "Alice", "initialDeposit": 100}`, which returns `{"accountId": "..."}`
- Query: `GET /api/accounts/{id}`, which returns the account

---

## Level 1: split the code, one database, one table

Commands and queries are separate classes, but they use the same `account` table.

### Flow

```
COMMAND  POST /api/accounts
   |
   v
AccountCommandController
   |
   v
AccountCommandHandler ---- save ----> [ account table ]
   |                                        ^
   v                                        |
returns 201 + accountId only                |
(never account data)                        |
                                            |
QUERY    GET /api/accounts/{id}             |
   |                                        |
   v                                        |
AccountQueryController                      |
   |                                        |
   v                                        |
AccountQueryService                         |
   |                                        |
   v                                        |
AccountViewRepository ---- select ----------+
   |    (no save/delete, selects straight into a DTO)
   v
returns AccountView (DTO, never the entity)
```

Rules:
- The command side changes data and returns only an id.
- The query side returns data and changes nothing.
- They share the table, but not code.

### How to test

Start it:

```bash
mvn -pl level-1-split-code spring-boot:run
```

Open an account (command):

```bash
curl -i -X POST localhost:8081/api/accounts -H 'Content-Type: application/json' -d '{"owner":"Alice","initialDeposit":100}'
```

Expected: `HTTP/1.1 201`, a `Location` header and `{"accountId":"<id>"}`.

Read it back (query). Replace `<id>` with the id from above:

```bash
curl localhost:8081/api/accounts/<id>
```

Expected:

```
{"accountId":"<id>","owner":"Alice","balance":100.00,"openedAt":"..."}
```

Other cases:
- Unknown id gives `404` with a "not found" message.
- Invalid body (blank owner or negative deposit) gives `400`.

Look at the table: open http://localhost:8081/h2-console, use JDBC URL
`jdbc:h2:mem:bank1` and user `sa`, then run `select * from account`.

---

## Level 2: one database, separate write table and read table

Each side now owns its own table. The command side writes `account`. The query
side reads `account_summary`. An event keeps them in sync, in the same transaction.

### Flow

```
COMMAND  POST /api/accounts
   |
   v
AccountCommandController
   |
   v
AccountCommandHandler
   |  1. save ---------------------------> [ account ]          (write table)
   |  2. publish AccountOpened event
   |         |
   |         v   (synchronous, same transaction)
   |    AccountProjection
   |         |  save ----------------------> [ account_summary ]  (read table)
   |
   v
commit (both tables together, or neither)
   |
   v
returns 201 + accountId


QUERY    GET /api/accounts/{id}
   |
   v
AccountQueryController -> AccountQueryService
   |
   v
select from [ account_summary ] only
   |
   v
returns AccountSummary
```

What's different from Level 1:
- The query side never touches the `account` table.
- The command side doesn't know `account_summary` exists. It only publishes an event.
- The read is always up to date right after the write, because both tables commit in one transaction.

### How to test

Start it:

```bash
mvn -pl level-2-separate-tables spring-boot:run
```

Open an account:

```bash
curl -i -X POST localhost:8082/api/accounts -H 'Content-Type: application/json' -d '{"owner":"Alice","initialDeposit":100}'
```

Expected: `HTTP/1.1 201` and `{"accountId":"<id>"}`.

Read it back, right away:

```bash
curl localhost:8082/api/accounts/<id>
```

Expected:

```
{"accountId":"<id>","owner":"Alice","balance":100.00,"openedAt":"..."}
```

Unknown id gives `404`, and an invalid body gives `400`.

See both tables: open http://localhost:8082/h2-console, use JDBC URL
`jdbc:h2:mem:bank2` and user `sa`, then run:

```sql
select * from account;          -- write table
select * from account_summary;  -- read table
```

---

## Level 3: separate write database and read database, synced through RabbitMQ

The two sides now have their own databases. The command side uses JPA on the
write database. The query side uses plain SQL (`JdbcClient`) on the read
database. RabbitMQ carries events between them.

### Flow

```
COMMAND  POST /api/accounts
   |
   v
AccountCommandController
   |
   v
AccountCommandHandler
   |  1. save ------------------------> (( bank3_write DB ))  [ account ]
   |  2. raise AccountOpened event
   v
commit
   |
   +--> returns 202 Accepted + accountId   (read side may not have it yet)
   |
   v   (only after commit)
AccountEventPublisher
   |  send JSON
   v
+----------------------------+
|  RabbitMQ                  |
|  queue: account-events     |
+----------------------------+
   |  deliver (async)
   v
AccountProjection (@RabbitListener)
   |  merge -----------------------> (( bank3_read DB ))  [ account_summary ]


QUERY    GET /api/accounts/{id}
   |
   v
AccountQueryController -> AccountQueryService
   |
   v
select from (( bank3_read DB )) [ account_summary ] only
   |
   v
returns AccountSummary   (or 404 if the event hasn't arrived yet)
```

What's different from Level 2:
- Two databases. The query side can't see the write database at all.
- The read side is updated asynchronously, so it's **eventually consistent**.
  That's why the command returns `202 Accepted` instead of `201 Created`.
- The read database's table comes from a Liquibase migration:
  `src/main/resources/db/changelog/read-db-changelog.yaml`.
  The write database's table is still created by Hibernate.
- Demo shortcut: if the app crashes between the commit and the send to RabbitMQ,
  the event is lost and the read database never gets the account. Real systems
  fix this with the outbox pattern.

### How to test

1. Start RabbitMQ with Podman (from the project root). On macOS, Podman needs a
   machine first. This is a one-time setup, and the first command downloads a VM image:

```bash
podman machine init
```

```bash
podman machine start
```

```bash
podman compose up -d
```

   If `podman compose` isn't available, run the container directly instead:

```bash
podman run -d --name cqrs-rabbitmq -p 5672:5672 -p 15672:15672 docker.io/library/rabbitmq:3-management
```

   RabbitMQ UI: http://localhost:15672 (guest / guest).

2. Start the app:

```bash
mvn -pl level-3-separate-database spring-boot:run
```

3. Open an account:

```bash
curl -i -X POST localhost:8083/api/accounts -H 'Content-Type: application/json' -d '{"owner":"Alice","initialDeposit":100}'
```

   Expected: `HTTP/1.1 202`, a `Location` header and `{"accountId":"<id>"}`.

4. Read it back:

```bash
curl localhost:8083/api/accounts/<id>
```

   Expected (usually within milliseconds):

```
{"accountId":"<id>","owner":"Alice","balance":100.00,"openedAt":"..."}
```

   If you call it fast enough, you may get a `404` first. That's eventual
   consistency: the event is still on its way through RabbitMQ.

5. See the message traffic: in the RabbitMQ UI, open **Queues** and then
   `account-events`. The message rate goes up each time you open an account.

6. See the two databases: open http://localhost:8083/h2-console with user `sa`:
   - JDBC URL `jdbc:h2:mem:bank3_write`, then `select * from account;`
   - JDBC URL `jdbc:h2:mem:bank3_read`, then `select * from account_summary;`
     (you'll also see Liquibase's `databasechangelog` tables here)

7. Stop RabbitMQ when you're done:

```bash
podman compose down
```
