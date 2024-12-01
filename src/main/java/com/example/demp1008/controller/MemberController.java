package com.example.demp1008.controller;

import com.example.demp1008.entity.Member;
import com.example.demp1008.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create")
    public Member createMember(Member member){
        return memberService.createMember(member);
    }

}
