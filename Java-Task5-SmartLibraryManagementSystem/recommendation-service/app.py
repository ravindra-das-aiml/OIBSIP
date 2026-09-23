"""
Recommendation microservice for the Smart Library Management System.
Pure Python implementation (no numpy/scikit-learn) - TF-IDF + cosine similarity.
"""

import math
from collections import Counter
from flask import Flask, request, jsonify

app = Flask(__name__)


def tokenize(book):
    author = str(book.get("author", "")).lower().replace(",", "").split()
    category = str(book.get("category", "")).lower().split()
    return author + author + category


def compute_tfidf_vectors(catalog):
    doc_tokens = {book["id"]: tokenize(book) for book in catalog}
    n_docs = len(catalog)

    df = Counter()
    for tokens in doc_tokens.values():
        for term in set(tokens):
            df[term] += 1

    idf = {term: math.log((n_docs + 1) / (freq + 1)) + 1 for term, freq in df.items()}

    vectors = {}
    for book_id, tokens in doc_tokens.items():
        tf = Counter(tokens)
        total = len(tokens) if tokens else 1
        vectors[book_id] = {term: (count / total) * idf[term] for term, count in tf.items()}

    return vectors


def cosine_similarity(vec_a, vec_b):
    common_terms = set(vec_a.keys()) & set(vec_b.keys())
    dot_product = sum(vec_a[t] * vec_b[t] for t in common_terms)

    norm_a = math.sqrt(sum(v * v for v in vec_a.values()))
    norm_b = math.sqrt(sum(v * v for v in vec_b.values()))

    if norm_a == 0 or norm_b == 0:
        return 0.0
    return dot_product / (norm_a * norm_b)


def average_vector(vectors):
    combined = Counter()
    for vec in vectors:
        for term, weight in vec.items():
            combined[term] += weight
    n = len(vectors) if vectors else 1
    return {term: total / n for term, total in combined.items()}


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

    vectors = compute_tfidf_vectors(catalog)

    history_vectors = [vectors[bid] for bid in history_ids if bid in vectors]
    if not history_vectors:
        return jsonify({"recommendations": [], "reason": "history books not found in catalog"}), 200

    user_profile = average_vector(history_vectors)

    scored = []
    for book in catalog:
        if book["id"] in history_ids:
            continue
        score = cosine_similarity(user_profile, vectors[book["id"]])
        if score > 0:
            scored.append((book, score))

    scored.sort(key=lambda pair: pair[1], reverse=True)
    top_results = scored[:top_n]

    recommendations = [
        {
            "id": book["id"],
            "title": book.get("title"),
            "author": book.get("author"),
            "category": book.get("category"),
            "score": round(score, 4),
        }
        for book, score in top_results
    ]

    return jsonify({"recommendations": recommendations})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
