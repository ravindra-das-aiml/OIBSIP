package com.oibsip.library.service;

import com.oibsip.library.dto.RecommendedBookDto;
import com.oibsip.library.model.Book;
import com.oibsip.library.model.IssueRecord;
import com.oibsip.library.model.Member;
import com.oibsip.library.repository.BookRepository;
import com.oibsip.library.repository.IssueRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RecommendationClient — the bridge between the Java backend and the Python
 * recommendation microservice (Flask + scikit-learn).
 *
 * Flow:
 *  1. Build the full book catalog + this member's issue history from the DB
 *  2. POST it to the Flask /recommend endpoint
 *  3. Flask computes content-based similarity (TF-IDF + cosine similarity
 *     over "author + category" text) and returns ranked book IDs
 *  4. Map the response back into RecommendedBookDto objects for the frontend
 *
 * If the microservice is unreachable, this degrades gracefully to an empty
 * list rather than breaking the core library functionality — recommendations
 * are a bonus feature, not a dependency for issuing/returning books.
 */
@Service
public class RecommendationClient {

    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;
    private final IssueRecordRepository issueRecordRepository;

    @Value("${recommendation.service.url}")
    private String recommendationServiceUrl;

    public RecommendationClient(RestTemplate restTemplate,
                                 BookRepository bookRepository,
                                 IssueRecordRepository issueRecordRepository) {
        this.restTemplate = restTemplate;
        this.bookRepository = bookRepository;
        this.issueRecordRepository = issueRecordRepository;
    }

    public List<RecommendedBookDto> getRecommendationsForMember(Member member, int topN) {
        List<Book> catalog = bookRepository.findAll();
        List<IssueRecord> history = issueRecordRepository.findByMember(member);

        List<Long> historyIds = history.stream()
                .map(r -> r.getBook().getId())
                .distinct()
                .collect(Collectors.toList());

        if (historyIds.isEmpty()) {
            return Collections.emptyList(); // nothing to base recommendations on yet
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("catalog", catalog.stream().map(this::toCatalogEntry).collect(Collectors.toList()));
        requestBody.put("history_ids", historyIds);
        requestBody.put("top_n", topN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    recommendationServiceUrl + "/recommend", request, Map.class);

            if (response == null || !response.containsKey("recommendations")) {
                return Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recs = (List<Map<String, Object>>) response.get("recommendations");

            return recs.stream().map(this::toDto).collect(Collectors.toList());

        } catch (Exception e) {
            // Recommendation service down/unreachable — fail gracefully, log for debugging
            System.err.println("⚠️ Recommendation service unavailable: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Map<String, Object> toCatalogEntry(Book book) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("id", book.getId());
        entry.put("title", book.getTitle());
        entry.put("author", book.getAuthor());
        entry.put("category", book.getCategory());
        return entry;
    }

    @SuppressWarnings("unchecked")
    private RecommendedBookDto toDto(Map<String, Object> raw) {
        RecommendedBookDto dto = new RecommendedBookDto();
        dto.setBookId(Long.valueOf(raw.get("id").toString()));
        dto.setTitle((String) raw.get("title"));
        dto.setAuthor((String) raw.get("author"));
        dto.setCategory((String) raw.get("category"));
        dto.setSimilarityScore(Double.parseDouble(raw.get("score").toString()));
        return dto;
    }
}
