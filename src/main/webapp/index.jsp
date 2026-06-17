<%

    String redirectURL = request.getContextPath() + "/app?command=appointments-list";
    response.sendRedirect(redirectURL);
%>