package com.oibsip.library.repository;

import com.oibsip.library.model.IssueRecord;
import com.oibsip.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
    List<IssueRecord> findByMember(Member member);
    List<IssueRecord> findByMemberAndStatus(Member member, IssueRecord.Status status);
    List<IssueRecord> findByStatus(IssueRecord.Status status);
}
