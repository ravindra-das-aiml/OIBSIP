"""
Recommendation microservice for the Smart Library Management System.

Content-based filtering approach:
  1. Represent every book as a text "profile" combining its author + category
  2. Vectorize all book profiles using TF-IDF
  3. For a given user, average the TF-IDF vectors of the books they've already
     read (their "taste profile")
  4. Rank all *other* books in the catalog by cosine similarity to that taste
     profile, and return the top N

This is intentionally simple and explainable (no black-box deep learning) —
appropriate for a catalog of this size, and easy to explain in an interview:
"a book similar to what you already liked, based on shared author/category."

Run:
    pip install -r requirements.txt
    python app.py
Service listens on http://localhost:5000
"""

from flask import Flask, request, jsonify
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

app = Flask(__name__)


def build_profile_text(book: dict) -> str:
    """Combines author + category into one text field for TF-IDF.
    Author is repeated to weight it slightly higher than category alone."""
    author = str(book.get("author", "")).replace(" ", "_")
    category = str(book.get("category", ""))
    return f"{author} {author} {category}"


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "service": "library-recommendation-engine"})


@app.route("/recommend", methods=["POST"])
def recommend():
    data = request.get_json(force=True)

    catalog = data.get("catalog", [])
    history_ids = set(data.get("history_ids", []))
    top_n = int(data.get("top_n", 5))

    if not catalog:
        return jsonify({"recommendations": [], "reason": "empty catalog"}), 200

    if not history_ids:
        return jsonify({"recommendations": [], "reason": "no reading history yet"}), 200

    # Build TF-IDF matrix over the whole catalog
    profiles = [build_profile_text(b) for b in catalog]
    vectorizer = TfidfVectorizer()
    tfidf_matrix = vectorizer.fit_transform(profiles)

    # Index lookup
    id_to_index = {book["id"]: i for i, book in enumerate(catalog)}

    history_indices = [id_to_index[bid] for bid in history_ids if bid in id_to_index]
    if not history_indices:
        return jsonify({"recommendations": [], "reason": "history books not found in catalog"}), 200

    # User's "taste profile" = average vector of books they've read
    history_vectors = tfidf_matrix[history_indices]
    user_profile = np.asarray(history_vectors.mean(axis=0))

    # Similarity of every book in the catalog to the user's taste profile
    similarities = cosine_similarity(user_profile, tfidf_matrix)[0]

    # Rank books, excluding ones already read
    ranked = sorted(
        (
            (book, float(similarities[i]))
            for i, book in enumerate(catalog)
            if book["id"] not in history_ids
        ),
        key=lambda pair: pair[1],
        reverse=True,
    )

    top_results = ranked[:top_n]

    recommendations = [
        {
            "id": book["id"],
            "title": book.get("title"),
            "author": book.get("author"),
            "category": book.get("category"),
            "score": round(score, 4),
        }
        for book, score in top_results
        if score > 0  # don't recommend completely unrelated books
    ]

    return jsonify({"recommendations": recommendations})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
