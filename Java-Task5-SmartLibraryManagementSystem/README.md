# Smart Library Management System (with AI Book Recommendations)

Built for the Oasis Infobyte Java Development Internship (Task 5), extended
with a content-based recommendation microservice.

## Core Features (OIBSIP Requirement)

**Admin Module**
- Admin login (separate from user registration flow)
- Add / edit / delete books
- View all currently issued books and due dates
- View registered members
- Mark fines as paid

**User Module**
- User registration and login
- Browse catalogue by category, search by title/author
- Issue a book (decrements available quantity, sets a 14-day due date)
- Return a book (increments quantity, auto-calculates fine if overdue at ₹5/day)
- Advance booking / reservation for books currently issued to someone else

## AI/ML Enhancement (Bonus) — Recommendation Microservice

The standard checklist only asks for CRUD + fines. On top of that, this
project adds a **"Recommended for You"** feature powered by a separate
Python microservice, demonstrating a realistic multi-service architecture:

```
┌─────────────┐        REST/JSON        ┌──────────────────────┐
│  Java Spring │ ──────────────────────► │  Python Flask service │
│  Boot backend│ ◄────────────────────── │  (TF-IDF + cosine sim) │
└─────────────┘                         └──────────────────────┘
```

**How it works:**
1. Whenever a member views their dashboard, the Java backend sends the full
   book catalog + that member's reading history to the Flask service
2. Flask builds a TF-IDF vector for each book from its `author + category`
3. It computes the member's "taste profile" (average vector of books they've
   read) and ranks every other book by cosine similarity to it
4. The top matches are returned and shown as recommendations

This is **content-based filtering** — chosen over collaborative filtering
because the catalog/user base here is small (collaborative filtering needs
many users' overlapping behaviour to work well; content-based works from
day one with just one user's history).

If the Python service is down, the Java backend fails gracefully — the core
library features keep working without recommendations.

## Tech Stack
- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, H2 (dev) / MySQL (prod)
- **Recommendation Service:** Python 3, Flask, scikit-learn, NumPy
- **Frontend:** HTML/CSS/JavaScript (vanilla, fetch API)

## How to Run

**1. Start the recommendation microservice**
```bash
cd recommendation-service
pip install -r requirements.txt
python app.py
# runs on http://localhost:5000
```

**2. Start the Spring Boot backend**
```bash
cd backend
mvn spring-boot:run
# runs on http://localhost:8080
```

**3. Open the frontend**
Just open `frontend/index.html` in a browser.
Sample login: `ravindra@example.com` / `pass123`
Admin login: `admin@library.com` / `admin123`

## Project Structure
```
Smart-Library/
├── backend/                      (Java Spring Boot)
│   ├── pom.xml
│   └── src/main/java/com/oibsip/library/
│       ├── model/                (Book, Member, IssueRecord)
│       ├── repository/           (JPA repositories)
│       ├── service/               (LibraryService, RecommendationClient)
│       ├── controller/           (REST endpoints)
│       └── config/                (AppConfig, DataSeeder)
├── recommendation-service/       (Python Flask)
│   ├── app.py
│   └── requirements.txt
├── frontend/
│   └── index.html
└── README.md
```

## API Quick Reference
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/members/register` | Register a new user |
| POST | `/api/members/login` | Login |
| GET | `/api/books` | List all books |
| GET | `/api/books/search?keyword=` | Search by title/author |
| POST | `/api/issue?memberId=&bookId=` | Issue a book |
| POST | `/api/return/{issueRecordId}` | Return a book |
| POST | `/api/reserve?memberId=&bookId=` | Reserve a book |
| GET | `/api/members/{id}/recommendations` | AI recommendations |

## Notes for Submission
- For the real MySQL setup, uncomment the MySQL block in
  `application.properties` and comment out the H2 block
- Passwords are stored in plain text in this demo for simplicity — for a
  production-grade submission, add `BCryptPasswordEncoder` in
  `MemberController`
