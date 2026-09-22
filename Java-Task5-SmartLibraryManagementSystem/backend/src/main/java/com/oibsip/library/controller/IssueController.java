package com.oibsip.library.controller;

import com.oibsip.library.dto.RecommendedBookDto;
import com.oibsip.library.model.IssueRecord;
import com.oibsip.library.model.Member;
import com.oibsip.library.repository.MemberRepository;
import com.oibsip.library.service.LibraryService;
import com.oibsip.library.service.RecommendationClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IssueController {

    private final LibraryService libraryService;
    private final RecommendationClient recommendationClient;
    private final MemberRepository memberRepository;

    public IssueController(LibraryService libraryService,
                            RecommendationClient recommendationClient,
                            MemberRepository memberRepository) {
        this.libraryService = libraryService;
        this.recommendationClient = recommendationClient;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/issue")
    public IssueRecord issueBook(@RequestParam Long memberId, @RequestParam Long bookId) {
        return libraryService.issueBook(memberId, bookId);
    }

    @PostMapping("/return/{issueRecordId}")
    public IssueRecord returnBook(@PathVariable Long issueRecordId) {
        return libraryService.returnBook(issueRecordId);
    }

    @PostMapping("/reserve")
    public IssueRecord reserveBook(@RequestParam Long memberId, @RequestParam Long bookId) {
        return libraryService.reserveBook(memberId, bookId);
    }

    @PostMapping("/fine/{issueRecordId}/pay")
    public void payFine(@PathVariable Long issueRecordId) {
        libraryService.payFine(issueRecordId);
    }

    @GetMapping("/members/{memberId}/history")
    public List<IssueRecord> getHistory(@PathVariable Long memberId) {
        return libraryService.getMemberHistory(memberId);
    }

    @GetMapping("/admin/issued")
    public List<IssueRecord> getAllIssued() {
        return libraryService.getAllIssuedBooks();
    }

    /**
     * The AI/ML enhancement endpoint — "Recommended for you", powered by the
     * Python content-based recommendation microservice.
     */
    @GetMapping("/members/{memberId}/recommendations")
    public List<RecommendedBookDto> getRecommendations(@PathVariable Long memberId,
                                                         @RequestParam(defaultValue = "5") int topN) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return recommendationClient.getRecommendationsForMember(member, topN);
    }
}
