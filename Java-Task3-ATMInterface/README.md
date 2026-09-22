# ATM Interface — with Rule-Based Fraud Detection

A console-based Java ATM simulation built for the Oasis Infobyte Java Development
Internship (Task 3), extended with a transaction anomaly-detection layer.

## Core Features (OIBSIP Requirement)
- Login with User ID + PIN, locked out after 3 failed attempts
- Main menu: Transaction History, Withdraw, Deposit, Transfer, Quit
- Balance check before withdrawal/transfer — "Insufficient Funds" handling
- Full transaction history stored in an `ArrayList`
- Clean OOP design across 5 classes: `Account`, `Transaction`, `Bank`, `ATM`, `Main`

## AI/ML Enhancement (Bonus) — `FraudDetector.java`
On top of the required checklist, this project adds a **rule-based anomaly
detection layer** that evaluates every withdrawal before it completes.
It's deliberately rule-based rather than a black-box model — every flag is
fully explainable, which mirrors how real risk-scoring systems are designed
to be auditable.

Three independent rules run on each withdrawal:

| Rule | Logic | Why it matters |
|---|---|---|
| **Velocity check** | 3+ withdrawals within a 5-minute window | Catches rapid-fire draining of an account (e.g. stolen card) |
| **Amount deviation** | Withdrawal > 3× the account's historical average | Catches one unusually large withdrawal even if infrequent |
| **Off-hours check** | Transaction between 12 AM–5 AM | Lower-confidence signal, common in real fraud systems |

Flagged transactions are still completed (this is a *detection*, not a
*blocking*, system — matching how most banks operate: flag for review, don't
auto-reject) but are clearly marked in the transaction history with the
reason(s) that triggered the flag.

## Tech Stack
Java 17+ (Core Java, OOP, `java.time`, Streams — no external libraries)

## How to Run
```bash
cd src
javac *.java
java Main
```

**Sample accounts for testing:**
| Account ID | PIN | Balance |
|---|---|---|
| 1001 | 1234 | ₹50,000 |
| 1002 | 5678 | ₹20,000 |

## Project Structure
```
ATM-Interface/
├── src/
│   ├── Main.java
│   ├── ATM.java
│   ├── Bank.java
│   ├── Account.java
│   ├── Transaction.java
│   └── FraudDetector.java
└── README.md
```

## Possible Future Extensions
- Replace fixed thresholds with values learned from historical transaction data
- Persist accounts/transactions to a real database (MySQL/SQLite)
- Expose fraud alerts via email/SMS notification
