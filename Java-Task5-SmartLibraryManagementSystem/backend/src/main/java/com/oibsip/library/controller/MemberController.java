package com.oibsip.library.controller;

import com.oibsip.library.model.Member;
import com.oibsip.library.repository.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PostMapping("/register")
    public Member register(@RequestBody Member member) {
        // NOTE: for the real submission, hash the password (e.g. BCryptPasswordEncoder)
        // before saving — stored here in plain text only to keep the demo simple.
        return memberRepository.save(member);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        return memberRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .<Map<String, Object>>map(m -> Map.of(
                        "success", true,
                        "memberId", m.getId(),
                        "name", m.getName(),
                        "isAdmin", m.isAdmin()
                ))
                .orElse(Map.of("success", false, "message", "Invalid email or password"));
    }
}
