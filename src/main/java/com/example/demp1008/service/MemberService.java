package com.example.demp1008.service;

import com.example.demp1008.entity.Member;
import com.example.demp1008.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;


    //멤버생성
    public Member createMember(Member member){
        if(!memberRepository.existsByEmail(member.getEmail())){
            member.setBorrowedCnt(0);

            memberRepository.save(member);
        }

        return member;

    }

    // 멤버 로그인 추가
    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!member.getPassword().equals(password)) {  //암호화? 는 일단 패스하는 걸로
            throw new RuntimeException("Invalid password");
        }

        return member;
    }



}
