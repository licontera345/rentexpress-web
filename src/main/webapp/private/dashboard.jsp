<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/header.jsp" %>
<section class="private-section py-6">
    <div class="container">
        <header class="page-header">
            <span class="section-eyebrow"><fmt:message key="common.navigation.management" /></span>
            <h2 class="page-title"><fmt:message key="dashboard.title" /></h2>
            <p class="page-subtitle"><fmt:message key="dashboard.welcome" /></p>
        </header>
        <div class="grid three-columns dashboard-grid">
            <article class="card-common shadow-soft admin-card">
                <h3><fmt:message key="dashboard.employees" /></h3>
                <p><fmt:message key="dashboard.employees.desc" /></p>
                <a class="btn btn-outline" href="${ctx}/private/EmployeeServlet"><fmt:message key="dashboard.view" /></a>
            </article>
            <article class="card-common shadow-soft admin-card">
                <h3><fmt:message key="dashboard.vehicles" /></h3>
                <p><fmt:message key="dashboard.vehicles.desc" /></p>
                <a class="btn btn-outline" href="${ctx}/private/VehicleServlet"><fmt:message key="dashboard.view" /></a>
            </article>
            <article class="card-common shadow-soft admin-card">
                <h3><fmt:message key="dashboard.reports" /></h3>
                <p><fmt:message key="dashboard.reports.desc" /></p>
                <a class="btn btn-outline" href="${ctx}/private/dashboard.jsp"><fmt:message key="dashboard.view" /></a>
            </article>
        </div>
    </div>
</section>
<%@ include file="/common/footer.jsp" %>
