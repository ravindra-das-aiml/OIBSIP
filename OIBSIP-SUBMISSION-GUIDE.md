# How to Push These Projects to Your OIBSIP GitHub Repo

Your GitHub repo must be named exactly `OIBSIP` (all caps). Inside it, create
these two folders using the strict naming format from the task guide:

```
OIBSIP/
├── Java-Task3-ATMInterface/
│   ├── src/
│   │   ├── Main.java
│   │   ├── ATM.java
│   │   ├── Bank.java
│   │   ├── Account.java
│   │   ├── Transaction.java
│   │   └── FraudDetector.java
│   └── README.md
│
└── Java-Task5-SmartLibraryManagementSystem/
    ├── backend/          (Spring Boot)
    ├── recommendation-service/   (Flask)
    ├── frontend/          (HTML/CSS/JS)
    └── README.md
```

## Step-by-step

```bash
# 1. Create the OIBSIP repo on GitHub (via github.com, name it exactly OIBSIP)

# 2. Clone it locally
git clone https://github.com/<your-username>/OIBSIP.git
cd OIBSIP

# 3. Copy in the two project folders (from what Claude generated),
#    renaming them to match the required format exactly:
#      ATM-Interface/        -> Java-Task3-ATMInterface/
#      Smart-Library/        -> Java-Task5-SmartLibraryManagementSystem/

# 4. Commit and push
git add .
git commit -m "Add Java Task 3: ATM Interface with fraud detection"
git commit -m "Add Java Task 5: Smart Library Management System with AI recommendations"
git push origin main
```

## Before recording your demo video
1. **ATM Interface** — record: login → normal withdrawal → 3-4 rapid
   withdrawals to show the fraud flag trigger → transaction history showing
   the flagged entry. Explain out loud *why* it was flagged.
2. **Library System** — record: register/login → browse catalogue → issue
   2-3 books from the same category → show the "Recommended for You" section
   populate → explain the TF-IDF/cosine-similarity logic briefly.

Remember the video's first 2 seconds must show a static title card with your
**full name, track (Java Development), and task title**.

## Checklist before submitting
- [ ] Both projects pushed under `OIBSIP/` with correct folder names
- [ ] Each project has its own README.md (already included)
- [ ] Demo videos recorded (with title card) and posted to LinkedIn
- [ ] LinkedIn posts tag Oasis Infobyte + use `#oasisinfobyte` hashtag
- [ ] Commented on 2 other interns' demo videos
- [ ] Submitted via the official Task Submission Form with your OIBSIP repo link
