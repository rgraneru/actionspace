package actionspace.controller;

import actionspace.model.Invite;
import actionspace.service.EncryptorService;
import actionspace.service.InviteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.AuthenticationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.HashMap;

@Controller
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class WebController {

    @Autowired
    EncryptorService encryptorService;

    @Autowired
    InviteService inviteService;

    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    public WebController(InMemoryUserDetailsManager inMemoryUserDetailsManager) {
        this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
    }


    @GetMapping("/")
    public String invite(Model model) {
        model.addAttribute("invite", new Invite());
        model.addAttribute("projects", inviteService.getAllProjects());
        return "invite";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @PostMapping("/sendInvite")
    public String sendInvite(@RequestParam(name="name", required=false) String name, @Valid @NotBlank @RequestParam(name="email") String email, @RequestParam(name="projectId") String projectId, Model model) throws JSONException, UnsupportedEncodingException {
        inviteService.sendInvite(name, email, projectId, inviteService.getLoggedInUsername());
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("projectName", inviteService.getProjectName(projectId));
        model.addAttribute("invitedBy", inviteService.getLoggedInUsername());
        model.addAttribute("errorMessage", "errormessage");
        return "inviteSent";
    }

    @GetMapping("/registerNewUser")
    public String sendInvite(@RequestParam(name="token") String token, Model model) throws AuthenticationException, JSONException, IOException {
        String decrypt = encryptorService.decrypt(token);

        HashMap map = new ObjectMapper().readValue(decrypt, HashMap.class);
        String expiresString = (String) map.get("expires");
        Instant expires = Instant.parse(expiresString);
        if (Instant.now().isAfter(expires)) {
            throw new AuthenticationException("Token expired");
        }
        model.addAllAttributes(map);
        model.addAttribute("projectName", inviteService.getProjectName((String) map.get("projectId")));
        return "registerNewUser";
    }

    @PostMapping("addNewUser")
    public String addNewUser(@RequestParam(name="name", required=true) String name, @RequestParam(name="password", required=true) String password) {
                UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username(name)
                        .password(password)
                        .roles("USER")
                        .build();
        inMemoryUserDetailsManager.createUser(user);
        return "login";
    }
}