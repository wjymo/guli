package com.zzn.guli.member;

import com.zzn.guli.member.entity.MemberEntity;
import com.zzn.guli.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GuliMemberApplicationTests {

    @Autowired
    private MemberService memberService;
    @Test
    void contextLoads() {
        MemberEntity memberEntity=new MemberEntity().setCity("尚湖").setHeader("头");
        memberService.save(memberEntity);
    }

}
