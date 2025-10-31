<div class="card card-common mt-4 mt-lg-0">
    <div class="card-header"><fmt:message key="public.user.form.title" /></div>
    <div class="card-body">
        <p class="text-muted"><fmt:message key="public.user.form.description" /></p>
        <div class="d-grid gap-2">
            <a class="btn btn-outline-brand" href="${ctx}/app/users/register">
                <i class="bi bi-person-plus"></i> <fmt:message key="public.user.form.registerLink" />
            </a>
            <a class="btn btn-outline-secondary" href="${ctx}/login">
                <i class="bi bi-box-arrow-in-right"></i> <fmt:message key="public.user.form.loginLink" />
            </a>
        </div>
    </div>
</div>
