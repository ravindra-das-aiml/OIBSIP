package com.oibsip.library.dto;

/** Shape of each recommendation returned by the Flask recommendation service. */
public class RecommendedBookDto {

    private Long bookId;
    private String title;
    private String author;
    private String category;
    private double similarityScore;

    public RecommendedBookDto() {}

    // --- Getters & Setters ---
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }
}
