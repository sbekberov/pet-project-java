package spd.trello.security.extrafilter;

import spd.trello.domain.common.Domain;

import javax.servlet.http.HttpServletRequest;

public interface ExtraAuthorizationChecker <D extends Domain>{

    void checkAuthority(HttpServletRequest request);


}
