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

            memberRepository.save(member);
        }

        return member;

    }

}
