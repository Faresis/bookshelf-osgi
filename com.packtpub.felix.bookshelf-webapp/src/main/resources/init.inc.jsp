<%@ include file="init-no-check.inc.jsp" %>
<%  // check session
    if (!sessionBean.isSessionValid()) {
        response.sendRedirect("login.jsp");
    }
%>