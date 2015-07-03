<%
    String flowerName = request.getParameter("flowerName");
    session.setAttribute("flower", flowerName);
%>
${sessionScope.flower == 'rose' ? 'Color: \"red\"' : 'Color: \"NOT red\"'}

