package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicatedMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicatedMember(Member member) {
        Member foundMember = memberRepository.findMemberByEmail(member.getEmail());
        if (foundMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
}
