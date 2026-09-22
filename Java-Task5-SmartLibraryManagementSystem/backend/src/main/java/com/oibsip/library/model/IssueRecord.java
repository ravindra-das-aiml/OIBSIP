package com.oibsip.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "issue_records")
public class IssueRecord {

    public enum Status { ISSUED, RETURNED, RESERVED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private double fineAmount = 0.0;
    private boolean finePaid = false;

    public static final int LOAN_PERIOD_DAYS = 14;
    public static final double FINE_PER_DAY = 5.0;

    public IssueRecord() {}

    public IssueRecord(Member member, Book book) {
        this.member = member;
        this.book = book;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
        this.status = Status.ISSUED;
    }

    /** Calculates and stores the fine based on how late the return is. */
    public void markReturned() {
        this.returnDate = LocalDate.now();
        this.status = Status.RETURNED;
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
        if (daysLate > 0) {
            this.fineAmount = daysLate * FINE_PER_DAY;
        }
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Book getBook() { return book; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public double getFineAmount() { return fineAmount; }
    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }
}
