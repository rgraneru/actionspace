package actionspace.service;

import actionspace.model.Project;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class InviteService {
    @Autowired
    EncryptorService encryptorService;

    private final ArrayList<Project> projects;

    public InviteService() {
        projects = new ArrayList<>();
        projects.add(new Project("1", "Graving"));
        projects.add(new Project("2", "Bygging"));
        projects.add(new Project("3", "Riving"));
    }

    public List<Project> getAllProjects() {
        return projects;
    }

    public String getProjectName(String id) {
        //burde brukt map
        return projects.stream()
                .filter(project -> project.getId().equals(id))
                .map(Project::getName).findFirst()
                .get();
    }

    public String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public void sendInvite(String name, String email, String projectId, String loggedInUsername) throws JSONException, UnsupportedEncodingException {
        String inviteJson = createInviteJson(name, email, projectId, loggedInUsername);
        String encrypted = encryptorService.encrypt(inviteJson);
        String url = createLinkUrlEncodeToken(encrypted);
        Logger.getAnonymousLogger().log(Level.INFO, url);
    }

    private String createLinkUrlEncodeToken(String encrypted) throws UnsupportedEncodingException {
        return "http://localhost:8080/registerNewUser?token=" + URLEncoder.encode(encrypted, "UTF-8");
    }

    private String createInviteJson(String name, String email, String projectId, String loggedInUsername) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("email", email);
        jsonObject.put("projectId", projectId);
        jsonObject.put("inviter", loggedInUsername);
        jsonObject.put("expires", Instant.now().plus(2, ChronoUnit.DAYS));
        return jsonObject.toString();
    }
}
