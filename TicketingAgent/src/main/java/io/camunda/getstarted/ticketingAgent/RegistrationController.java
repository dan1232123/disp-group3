package io.camunda.getstarted.ticketingAgent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/members")
public class RegistrationController {

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/register")
    public Member registerMember(@RequestBody MemberRegistrationDto registrationDto) {
        // Check if email exists
        if (memberRepository.existsByEmail(registrationDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        // Create new member
        Member newMember = new Member();
        newMember.setEmail(registrationDto.getEmail());
        newMember.setName(registrationDto.getName());
        newMember.setPhone(registrationDto.getPhone());

        return memberRepository.save(newMember);
    }
}

class MemberRegistrationDto {
    private String email;
    private String name;
    private String phone;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone;}

}