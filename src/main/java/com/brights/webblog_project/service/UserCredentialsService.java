package com.brights.webblog_project.service;

import com.brights.webblog_project.model.PostComment;
import com.brights.webblog_project.model.User;
import com.brights.webblog_project.model.UserCredentials;

import java.util.List;

public interface UserCredentialsService {

    UserCredentials saveUserCredentials(UserCredentials userCredentials);
    UserCredentials getDetails(String username);
    UserCredentials getDetailsById(long id);
    String getUserCredentialsRoles(String username);
    List<UserCredentials> getAllUserCred();

    UserCredentials GetByUser(User user);

}
