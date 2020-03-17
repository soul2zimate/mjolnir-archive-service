package org.jboss.mjolnir.archive.service.webapp.servlet;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.jboss.logging.Logger;
import org.jboss.set.mjolnir.archive.domain.RegisteredUser;
import org.jboss.set.mjolnir.archive.domain.repositories.RegisteredUserRepositoryBean;
import org.jboss.set.mjolnir.archive.github.CustomizedTeamService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@WebServlet("members-report")
public class MembersReportServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private RegisteredUserRepositoryBean userRepositoryBean;

    @Inject
    private GitHubClient gitHubClient;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CustomizedTeamService teamService = new CustomizedTeamService(gitHubClient);

        List<Team> teams = teamService.getTeams("jbossas");

        Set<User> allMembers = new HashSet<>();
        Map<User, List<String>> usersTeams = new HashMap<>();
        for (Team team : teams) {
            List<User> teamMembers = teamService.getMembers("jbossas", team.getId());
            allMembers.addAll(teamMembers);
            teamMembers.forEach(member -> {
                if (usersTeams.containsKey(member)) {
                    usersTeams.get(member).add(team.getName());
                } else {
                    ArrayList<String> teamList = new ArrayList<>();
                    teamList.add(team.getName());
                    usersTeams.put(member, teamList);
                }
            });
        }
        logger.infof("All members: %d (unique %d)", allMembers.size(), uniqueLogins(allMembers));

        Set<User> knownUsers = new HashSet<>();
        Set<User> unknownUsers = new HashSet<>();
        allMembers.forEach(member -> {
            Optional<RegisteredUser> registeredUser = userRepositoryBean.findByGitHubUsername(member.getLogin());
            if (registeredUser.isPresent()) {
                knownUsers.add(member);
            } else {
                unknownUsers.add(member);
            }
        });
        logger.infof("Known members: %d (unique %d)", knownUsers.size(), uniqueLogins(knownUsers));
        logger.infof("Unknown members: %d (unique %d)", unknownUsers.size(), uniqueLogins(unknownUsers));

        resp.setContentType("text/plain");
        ServletOutputStream os = resp.getOutputStream();

        os.println("Unknown GitHub Teams Members");
        os.println("Login;Email;Name;Teams");
        for (User user: unknownUsers) {
            os.print(user.getLogin());
            os.print(";");
            os.print(user.getEmail());
            os.print(";");
            os.print(user.getName());
            os.print(";");
            os.print(StringUtils.joinWith(",", usersTeams.get(user).toArray()));
            os.println();
        }
    }

    private int uniqueLogins(Collection<User> users) {
        Set<String> uniqueLogins = new HashSet<>();
        users.forEach(m -> uniqueLogins.add(m.getLogin()));
        return uniqueLogins.size();
    }
}
