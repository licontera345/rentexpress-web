<div class="card card-common mt-4 mt-lg-0">
    <div class="card-header"><fmt:message key="employee.public.form.title" /></div>
    <div class="card-body">
        <p class="text-muted"><fmt:message key="employee.public.form.description" /></p>
        <div class="d-grid gap-2">
            <a class="btn btn-outline-brand" href="${ctx}/app/employees/private">
                <i class="bi bi-shield-lock"></i> <fmt:message key="employee.public.form.privateLink" />
            </a>
            <a class="btn btn-outline-secondary" href="${ctx}/app/users/register">
                <i class="bi bi-person-plus"></i> <fmt:message key="employee.public.form.userLink" />
            </a>
        </div>
    </div>
</div>
