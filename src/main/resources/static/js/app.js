const app = document.querySelector('#app');
const csrfState = { token: null };
const state = {
    user: null,
    profile: null,
    appointments: [],
    doctors: [],
    screen: 'overview',
    month: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
    selectedDate: localDateKey()
};

function localDateKey(date = new Date()) {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[character]);
}

function normalize(value) {
    return String(value ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR');
}

async function refreshCsrf() {
    const response = await fetch('/api/auth/csrf', { headers: { Accept: 'application/json' } });
    if (!response.ok) throw new Error('Não foi possível iniciar uma sessão segura.');
    const data = await response.json();
    csrfState.token = data.token;
}

async function api(url, options = {}) {
    const method = (options.method || 'GET').toUpperCase();
    const headers = { Accept: 'application/json', ...options.headers };
    if (options.body) headers['Content-Type'] = 'application/json';
    if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
        if (!csrfState.token) await refreshCsrf();
        headers['X-XSRF-TOKEN'] = csrfState.token;
    }
    const response = await fetch(url, { ...options, method, headers });
    const text = await response.text();
    let payload = null;
    if (text) {
        try { payload = JSON.parse(text); } catch { payload = text; }
    }
    if (!response.ok) {
        const message = typeof payload === 'string'
            ? payload
            : payload?.detail || payload?.message || payload?.title || '';
        throw new Error(message || `Não foi possível concluir a solicitação (${response.status}).`);
    }
    return payload;
}

function showToast(message, isError = false) {
    const region = document.querySelector('#toast-region');
    const toast = document.createElement('div');
    toast.className = `toast${isError ? ' error' : ''}`;
    toast.innerHTML = `<span class="toast-mark" aria-hidden="true">${isError ? '!' : '✓'}</span><span>${escapeHtml(message)}</span>`;
    region.append(toast);
    window.setTimeout(() => toast.remove(), 4500);
}

function renderAuth(errorMessage = '') {
    state.user = null;
    state.profile = null;
    state.appointments = [];
    app.innerHTML = `
        <div class="auth-page">
            <section class="auth-brand">
                <a class="brand" href="/" aria-label="Clínica, início">
                    <span class="brand-mark" aria-hidden="true">+</span>
                    <span><strong>Clínica</strong><small>SEU CUIDADO, MAIS PERTO</small></span>
                </a>
                <div class="auth-message">
                    <p class="eyebrow">PORTAL DE ATENDIMENTO</p>
                    <h1>Saúde em dia,<br>com simplicidade.</h1>
                    <p>Acesse sua agenda, encontre profissionais e acompanhe seus atendimentos em um só lugar.</p>
                </div>
                <span class="auth-footnote">Atendimento organizado para cuidar melhor.</span>
            </section>
            <section class="auth-panel">
                <div class="auth-card">
                    <div class="auth-tabs" role="tablist" aria-label="Acesso">
                        <button class="auth-tab active" type="button" data-auth-mode="login" role="tab" aria-selected="true">Entrar</button>
                        <button class="auth-tab" type="button" data-auth-mode="register" role="tab" aria-selected="false">Criar conta</button>
                    </div>
                    <div id="auth-form-area"></div>
                    <p class="auth-security"><span aria-hidden="true">▣</span> Seus dados são protegidos por conexão segura.</p>
                </div>
            </section>
        </div>`;
    renderAuthForm('login', errorMessage);
}

function renderAuthForm(mode, errorMessage = '') {
    document.querySelectorAll('[data-auth-mode]').forEach(tab => {
        const active = tab.dataset.authMode === mode;
        tab.classList.toggle('active', active);
        tab.setAttribute('aria-selected', String(active));
    });
    const area = document.querySelector('#auth-form-area');
    if (!area) return;
    if (mode === 'register') {
        area.innerHTML = `
            <p class="eyebrow">BEM-VINDO À CLÍNICA</p><h2>Crie sua conta</h2>
            <p class="auth-description">Cadastre-se como cliente para agendar seus atendimentos.</p>
            <form id="register-form" class="portal-form">
                ${inputField('Nome completo', 'name', 'text', true, '', 'autocomplete="name"')}
                ${inputField('CPF', 'cpf', 'text', true, '000.000.000-00')}
                ${inputField('E-mail', 'email', 'email', true, 'voce@email.com', 'autocomplete="email"')}
                ${inputField('Senha', 'password', 'password', true, '', 'minlength="10" autocomplete="new-password"', 'Use pelo menos 10 caracteres.')}
                ${errorMessage ? `<p class="form-error" role="alert">${escapeHtml(errorMessage)}</p>` : ''}
                <button class="button button-primary button-wide" type="submit">Criar conta</button>
            </form>
            <p class="doctor-access">É profissional da clínica? <button type="button" data-auth-mode="login" data-account-type="DOUTOR">Entre como doutor</button></p>`;
    } else {
        area.innerHTML = `
            <p class="eyebrow">BEM-VINDO DE VOLTA</p><h2>Acesse sua conta</h2>
            <p class="auth-description">Entre para acompanhar sua agenda e seus atendimentos.</p>
            <form id="login-form" class="portal-form">
                <div class="form-field"><label for="accountType">Tipo de conta</label><select class="form-control" id="accountType" name="accountType" required><option value="CLIENTE">Cliente</option><option value="DOUTOR">Doutor</option></select></div>
                ${inputField('E-mail', 'email', 'email', true, 'voce@email.com', 'autocomplete="username"')}
                ${inputField('Senha', 'password', 'password', true, '', 'autocomplete="current-password"')}
                ${errorMessage ? `<p class="form-error" role="alert">${escapeHtml(errorMessage)}</p>` : ''}
                <button class="button button-primary button-wide" type="submit">Entrar</button>
            </form>
            <p class="doctor-access">Ainda não tem conta? <button type="button" data-auth-mode="register">Cadastre-se como cliente</button></p>`;
    }
}

function inputField(label, name, type, required, placeholder = '', attributes = '', hint = '') {
    return `<div class="form-field"><label for="${name}">${label}</label><input class="form-control" id="${name}" name="${name}" type="${type}" ${required ? 'required' : ''} ${placeholder ? `placeholder="${escapeHtml(placeholder)}"` : ''} ${attributes}>${hint ? `<p class="form-hint">${escapeHtml(hint)}</p>` : ''}</div>`;
}

function accountIsClient() {
    return state.user?.accountType === 'CLIENTE';
}

function navItems() {
    const common = [
        ['overview', 'Visão geral', '⌂'],
        ['calendar', 'Minha agenda', '▦']
    ];
    if (accountIsClient()) common.push(['doctors', 'Buscar profissionais', '✚']);
    common.push(['settings', 'Configurações', '⚙']);
    return common;
}

function renderShell() {
    const items = navItems();
    const activeTitle = items.find(item => item[0] === state.screen)?.[1] || 'Visão geral';
    app.innerHTML = `
        <div class="app-shell">
            <aside class="sidebar" aria-label="Navegação principal">
                <a class="brand" href="/" aria-label="Clínica, página inicial">
                    <span class="brand-mark" aria-hidden="true">+</span>
                    <span><strong>Clínica</strong><small>PORTAL ${accountIsClient() ? 'DO CLIENTE' : 'DO DOUTOR'}</small></span>
                </a>
                <p class="nav-label">MENU</p>
                <nav class="main-nav">${items.map(([screen, label, icon]) => `
                    <button type="button" data-screen="${screen}" class="${state.screen === screen ? 'active' : ''}" ${state.screen === screen ? 'aria-current="page"' : ''}>
                        <span class="nav-icon" aria-hidden="true">${icon}</span>${label}
                    </button>`).join('')}</nav>
                <div class="sidebar-note"><span class="status-dot" aria-hidden="true"></span><span><strong>Conexão protegida</strong><small>Sessão autenticada</small></span></div>
            </aside>
            <div class="workspace">
                <header class="topbar">
                    <div class="breadcrumb"><span>Clínica</span><span aria-hidden="true">/</span><strong>${escapeHtml(activeTitle)}</strong></div>
                    <div class="user-actions"><span class="user-name">${escapeHtml(state.user?.name)}</span><button class="button button-quiet button-small" type="button" data-action="logout">Sair</button></div>
                </header>
                <main id="portal-content" class="content" tabindex="-1"></main>
                <footer class="footer">Portal de atendimento <span>•</span> Seus dados são pessoais e protegidos</footer>
            </div>
        </div>`;
    renderScreen();
}

function pageHeading(eyebrow, title, subtitle, action = '') {
    return `<div class="page-heading"><div><p class="eyebrow">${eyebrow}</p><h1>${title}</h1><p class="page-subtitle">${subtitle}</p></div>${action}</div>`;
}

function isPast(appointment) {
    return new Date(`${appointment.data}T${appointment.hora || '00:00:00'}`) < new Date();
}

function formatDate(value, options = { day: 'numeric', month: 'long', year: 'numeric' }) {
    const date = new Date(`${value}T12:00:00`);
    return Number.isNaN(date.getTime()) ? value : new Intl.DateTimeFormat('pt-BR', options).format(date);
}

function formatTime(value) {
    return String(value || '').slice(0, 5);
}

function appointmentCard(appointment, past = false, canCancel = false) {
    const personName = accountIsClient() ? appointment.doctorName : appointment.clientName;
    const label = accountIsClient() ? 'Doutor(a)' : 'Cliente';
    return `<article class="appointment-card ${past ? 'past' : ''}">
        <div class="appointment-date"><strong>${escapeHtml(formatDate(appointment.data, { day: '2-digit', month: 'short' }))}</strong><span>${escapeHtml(formatTime(appointment.hora))}</span></div>
        <div class="appointment-detail"><strong>${escapeHtml(appointment.descricao)}</strong><span>${label}: ${escapeHtml(personName || '—')}</span></div>
        <div class="appointment-status">${past ? '<span class="status-pill past-pill">Concluído</span>' : `<span class="status-pill upcoming-pill">Agendado</span>${canCancel ? `<button class="text-button" type="button" data-action="cancel" data-id="${escapeHtml(appointment.id)}">Cancelar</button>` : ''}`}</div>
    </article>`;
}

function renderOverview() {
    const upcoming = state.appointments.filter(appointment => !isPast(appointment))
        .sort((a, b) => `${a.data} ${a.hora}`.localeCompare(`${b.data} ${b.hora}`));
    const past = state.appointments.filter(isPast);
    const greeting = state.user?.name?.split(' ')[0] || 'Olá';
    const cards = accountIsClient()
        ? [['Próximos atendimentos', upcoming.length, '▦'], ['Atendimentos realizados', past.length, '✓'], ['Profissionais disponíveis', state.doctors.length, '✚']]
        : [['Atendimentos futuros', upcoming.length, '▦'], ['Atendimentos realizados', past.length, '✓'], ['Total na agenda', state.appointments.length, '◷']];
    return `${pageHeading('SEU ESPAÇO DE CUIDADO', `Olá, ${escapeHtml(greeting)}`, accountIsClient() ? 'Aqui está um resumo da sua jornada de atendimento.' : 'Acompanhe seus atendimentos e mantenha seu perfil atualizado.', accountIsClient() ? '<button class="button button-primary" type="button" data-screen="doctors">Encontrar profissional</button>' : '')}
        <section class="stats-grid">${cards.map(([label, value, icon]) => `<article class="stat-card"><div><span class="stat-label">${label}</span><strong class="stat-value">${value}</strong></div><span class="stat-icon" aria-hidden="true">${icon}</span></article>`).join('')}</section>
        <section class="panel"><div class="panel-heading"><div><h2>Próximos atendimentos</h2><p>Seus compromissos mais próximos</p></div><button type="button" class="button button-quiet button-small" data-screen="calendar">Abrir agenda</button></div>
        <div class="panel-content">${upcoming.length ? `<div class="appointment-list">${upcoming.slice(0, 4).map(item => appointmentCard(item, false, false)).join('')}</div>` : '<div class="table-empty"><strong>Nenhum atendimento futuro</strong><span>Seus próximos compromissos aparecerão aqui.</span></div>'}</div></section>`;
}

function appointmentGroups() {
    return state.appointments.reduce((groups, appointment) => {
        (groups[appointment.data] ||= []).push(appointment);
        return groups;
    }, {});
}

function renderCalendar() {
    const year = state.month.getFullYear();
    const month = state.month.getMonth();
    const first = new Date(year, month, 1);
    const offset = (first.getDay() + 6) % 7;
    const dayCount = new Date(year, month + 1, 0).getDate();
    const cellCount = Math.ceil((offset + dayCount) / 7) * 7;
    const groups = appointmentGroups();
    const cells = Array.from({ length: cellCount }, (_, index) => {
        const date = new Date(year, month, index - offset + 1);
        const key = localDateKey(date);
        const inMonth = date.getMonth() === month;
        const hasAppointments = Boolean(groups[key]?.length);
        return `<button type="button" class="calendar-day ${inMonth ? '' : 'outside'} ${key === state.selectedDate ? 'selected' : ''} ${hasAppointments ? 'has-appointments' : ''}" data-date="${key}" ${inMonth ? '' : 'aria-label="Dia fora do mês atual"'}><span>${date.getDate()}</span>${hasAppointments ? '<i aria-label="Tem atendimento"></i>' : ''}</button>`;
    }).join('');
    const selected = [...(groups[state.selectedDate] || [])].sort((a, b) => a.hora.localeCompare(b.hora));
    const allOrdered = [
        ...state.appointments.filter(isPast).sort((a, b) => `${b.data} ${b.hora}`.localeCompare(`${a.data} ${a.hora}`)),
        ...state.appointments.filter(appointment => !isPast(appointment))
            .sort((a, b) => `${a.data} ${a.hora}`.localeCompare(`${b.data} ${b.hora}`))
    ];
    return `${pageHeading('AGENDA', 'Minha agenda', 'Consulte seus atendimentos passados e futuros, com data e horário.')}
        <section class="calendar-panel">
            <div class="calendar-month"><button class="icon-button" type="button" data-action="month-prev" aria-label="Mês anterior">‹</button><h2>${escapeHtml(formatDate(`${year}-${String(month + 1).padStart(2, '0')}-01`, { month: 'long', year: 'numeric' }))}</h2><button class="icon-button" type="button" data-action="month-next" aria-label="Próximo mês">›</button><button class="button button-quiet button-small" type="button" data-action="month-today">Hoje</button></div>
            <div class="calendar-grid calendar-weekdays">${['Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb', 'Dom'].map(day => `<span>${day}</span>`).join('')}</div>
            <div class="calendar-grid">${cells}</div>
            <div class="selected-day"><h3>${escapeHtml(formatDate(state.selectedDate))}</h3>${selected.length ? `<div class="appointment-list">${selected.map(item => appointmentCard(item, isPast(item), accountIsClient())).join('')}</div>` : '<p class="empty-note">Não há atendimentos nesta data.</p>'}</div>
        </section>
        <section class="panel appointment-history"><div class="panel-heading"><div><h2>Todos os atendimentos</h2><p>Histórico recente e próximos compromissos</p></div><span class="page-subtitle">${allOrdered.length} ${allOrdered.length === 1 ? 'registro' : 'registros'}</span></div>
        <div class="panel-content">${allOrdered.length ? `<div class="appointment-list">${allOrdered.map(item => appointmentCard(item, isPast(item), accountIsClient())).join('')}</div>` : '<div class="table-empty"><strong>Sua agenda está vazia</strong><span>Os atendimentos aparecerão aqui.</span></div>'}</div></section>`;
}

async function searchDoctors(event) {
    event?.preventDefault();
    const form = document.querySelector('#doctor-search');
    if (!form) return;
    const values = new FormData(form);
    const date = values.get('data');
    const time = values.get('hora');
    if (Boolean(date) !== Boolean(time)) {
        showToast('Informe a data e o horário para filtrar por disponibilidade.', true);
        return;
    }
    const query = new URLSearchParams();
    const specialty = String(values.get('especialidade') || '').trim();
    if (specialty) query.set('especialidade', specialty);
    if (date && time) {
        query.set('data', date);
        query.set('hora', time.length === 5 ? `${time}:00` : time);
    }
    const submit = form.querySelector('[type="submit"]');
    submit.disabled = true;
    try {
        state.doctors = await api(`/api/doutores/directory${query.size ? `?${query}` : ''}`);
        drawDoctorResults();
    } catch (error) {
        showToast(error.message, true);
    } finally {
        submit.disabled = false;
    }
}

function drawDoctorResults() {
    const results = document.querySelector('#doctor-results');
    if (!results) return;
    const availableOnly = document.querySelector('#available-only')?.checked;
    const doctors = availableOnly ? state.doctors.filter(doctor => doctor.available === true) : state.doctors;
    results.innerHTML = doctors.length ? doctors.map(doctor => `
        <article class="doctor-card">
            <span class="doctor-avatar" aria-hidden="true">${escapeHtml(doctor.name.split(' ').map(part => part[0]).slice(0, 2).join('').toUpperCase())}</span>
            <div class="doctor-info"><h3>${escapeHtml(doctor.name)}</h3><p>${escapeHtml(doctor.areaDeAtuacao)}</p>${doctor.available === null ? '' : `<span class="availability ${doctor.available ? 'is-available' : 'is-busy'}">${doctor.available ? 'Horário disponível' : 'Horário ocupado'}</span>`}</div>
            <button class="button ${doctor.available === false ? 'button-quiet' : 'button-primary'} button-small" type="button" data-action="book" data-id="${escapeHtml(doctor.id)}" ${doctor.available === false ? 'disabled' : ''}>Agendar</button>
        </article>`).join('') : '<div class="table-empty"><strong>Nenhum profissional encontrado</strong><span>Ajuste os filtros e tente novamente.</span></div>';
}

function renderDoctors() {
    return `${pageHeading('ENCONTRE SEU PROFISSIONAL', 'Buscar profissionais', 'Filtre por especialidade e confira a disponibilidade no horário desejado.')}
        <section class="panel doctor-search-panel">
            <form id="doctor-search" class="doctor-search-form">
                <div class="form-field"><label for="specialty">Especialidade</label><input class="form-control" id="specialty" name="especialidade" type="search" placeholder="Ex.: Cardiologia"></div>
                <div class="form-field"><label for="search-date">Data desejada</label><input class="form-control" id="search-date" name="data" type="date" min="${localDateKey()}"></div>
                <div class="form-field"><label for="search-time">Horário desejado</label><input class="form-control" id="search-time" name="hora" type="time"></div>
                <button class="button button-primary" type="submit">Buscar</button>
            </form>
            <label class="check-label"><input id="available-only" type="checkbox"> Mostrar apenas horários disponíveis</label>
        </section>
        <div class="directory-heading"><div><h2>Profissionais</h2><p class="page-subtitle">Os resultados mostram todos os profissionais cadastrados.</p></div><span class="page-subtitle" id="doctor-count"></span></div>
        <section id="doctor-results" class="doctor-results"></section>`;
}

function renderSettings() {
    const profile = state.profile || {};
    const clientFields = accountIsClient()
        ? `${inputField('CPF', 'cpf', 'text', true, '', `value="${escapeHtml(profile.cpf)}"`)}` : '';
    const doctorFields = accountIsClient()
        ? '' : `${inputField('Área de atuação', 'areaDeAtuacao', 'text', true, '', `value="${escapeHtml(profile.areaDeAtuacao)}"`)}`;
    return `${pageHeading('SUA CONTA', 'Configurações', 'Atualize seus dados com segurança.')}
        <section class="settings-card"><div class="settings-intro"><span class="settings-icon" aria-hidden="true">♙</span><div><h2>Dados pessoais</h2><p>Confirme sua senha atual para salvar qualquer alteração.</p></div></div>
            <form id="profile-form" class="portal-form settings-form">
                ${inputField('Nome completo', 'name', 'text', true, '', `value="${escapeHtml(profile.name)}" autocomplete="name"`)}
                ${clientFields}
                ${doctorFields}
                ${inputField('E-mail', 'email', 'email', true, '', `value="${escapeHtml(profile.email)}" autocomplete="email"`)}
                <div class="form-divider"><span>Segurança da conta</span></div>
                ${inputField('Senha atual', 'currentPassword', 'password', true, '', 'autocomplete="current-password"')}
                ${inputField('Nova senha', 'newPassword', 'password', false, '', 'minlength="10" autocomplete="new-password"', 'Deixe em branco para manter a senha atual.')}
                <p id="profile-error" class="form-error" hidden role="alert"></p>
                <div class="settings-actions"><button class="button button-primary" type="submit">Salvar alterações</button></div>
            </form>
        </section>`;
}

function renderScreen() {
    const content = document.querySelector('#portal-content');
    if (!content) return;
    if (state.screen === 'calendar') content.innerHTML = renderCalendar();
    else if (state.screen === 'doctors' && accountIsClient()) content.innerHTML = renderDoctors();
    else if (state.screen === 'settings') content.innerHTML = renderSettings();
    else content.innerHTML = renderOverview();
    if (state.screen === 'doctors') {
        drawDoctorResults();
        document.querySelector('#doctor-count').textContent = `${state.doctors.length} ${state.doctors.length === 1 ? 'profissional' : 'profissionais'}`;
    }
}

async function loadPortal() {
    const profileUrl = accountIsClient() ? '/api/cliente/perfil' : '/api/doutor/perfil';
    const appointmentsUrl = accountIsClient() ? '/api/cliente/agendamentos' : '/api/doutor/agendamentos';
    const [profile, appointments] = await Promise.all([api(profileUrl), api(appointmentsUrl)]);
    state.profile = profile;
    state.appointments = appointments;
    if (accountIsClient()) {
        state.doctors = await api('/api/doutores/directory');
    }
    renderShell();
}

function openBooking(doctorId) {
    const doctor = state.doctors.find(item => String(item.id) === String(doctorId));
    if (!doctor) {
        showToast('Profissional não encontrado. Atualize a lista e tente novamente.', true);
        return;
    }
    const dialog = document.createElement('dialog');
    dialog.className = 'record-dialog';
    dialog.innerHTML = `
        <form id="booking-form" class="portal-form">
            <div class="dialog-heading"><div><p class="eyebrow">NOVO ATENDIMENTO</p><h2>Agendar consulta</h2><p class="page-subtitle">${escapeHtml(doctor.name)} · ${escapeHtml(doctor.areaDeAtuacao)}</p></div><button class="icon-button" type="button" data-action="close-dialog" aria-label="Fechar">×</button></div>
            ${inputField('Data', 'data', 'date', true, '', `min="${localDateKey()}" value="${escapeHtml(document.querySelector('#search-date')?.value || '')}"`)}
            ${inputField('Horário', 'hora', 'time', true, '', `value="${escapeHtml(document.querySelector('#search-time')?.value || '')}"`)}
            ${inputField('Motivo do atendimento', 'descricao', 'text', true, 'Ex.: Consulta de rotina', 'maxlength="255"')}
            <p id="booking-error" class="form-error" hidden role="alert"></p>
            <div class="dialog-actions"><button class="button button-quiet" type="button" data-action="close-dialog">Voltar</button><button class="button button-primary" type="submit">Confirmar agendamento</button></div>
        </form>`;
    dialog.dataset.doctorId = doctor.id;
    app.append(dialog);
    dialog.showModal();
    dialog.addEventListener('close', () => dialog.remove(), { once: true });
    dialog.querySelector('input[name="data"]').focus();
}

async function changeScreen(screen) {
    if (screen === 'doctors' && !accountIsClient()) return;
    state.screen = screen;
    renderShell();
    if (screen === 'doctors') {
        try {
            state.doctors = await api('/api/doutores/directory');
            drawDoctorResults();
            const count = document.querySelector('#doctor-count');
            if (count) count.textContent = `${state.doctors.length} ${state.doctors.length === 1 ? 'profissional' : 'profissionais'}`;
        } catch (error) { showToast(error.message, true); }
    }
}

app.addEventListener('click', async event => {
    const mode = event.target.closest('[data-auth-mode]');
    if (mode) {
        renderAuthForm(mode.dataset.authMode);
        if (mode.dataset.accountType) {
            const accountType = document.querySelector('#accountType');
            if (accountType) accountType.value = mode.dataset.accountType;
        }
        return;
    }
    const screen = event.target.closest('[data-screen]');
    if (screen) {
        await changeScreen(screen.dataset.screen);
        return;
    }
    const action = event.target.closest('[data-action]');
    if (!action) return;
    if (action.dataset.action === 'logout') {
        try {
            await api('/api/auth/logout', { method: 'POST' });
            csrfState.token = null;
            renderAuth();
        } catch (error) { showToast(error.message, true); }
    } else if (action.dataset.action === 'month-prev') {
        state.month = new Date(state.month.getFullYear(), state.month.getMonth() - 1, 1);
        renderScreen();
    } else if (action.dataset.action === 'month-next') {
        state.month = new Date(state.month.getFullYear(), state.month.getMonth() + 1, 1);
        renderScreen();
    } else if (action.dataset.action === 'month-today') {
        state.month = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
        state.selectedDate = localDateKey();
        renderScreen();
    } else if (action.dataset.action === 'book') {
        openBooking(action.dataset.id);
    } else if (action.dataset.action === 'close-dialog') {
        action.closest('dialog')?.close();
    } else if (action.dataset.action === 'cancel') {
        const appointment = state.appointments.find(item => String(item.id) === action.dataset.id);
        if (!appointment || !window.confirm(`Cancelar o atendimento de ${formatDate(appointment.data)} às ${formatTime(appointment.hora)}?`)) return;
        try {
            await api(`/api/cliente/agendamentos/${encodeURIComponent(appointment.id)}`, { method: 'DELETE' });
            await loadPortal();
            showToast('Atendimento cancelado.');
        } catch (error) { showToast(error.message, true); }
    }
});

app.addEventListener('click', event => {
    const day = event.target.closest('[data-date]');
    if (!day) return;
    state.selectedDate = day.dataset.date;
    const [year, month] = state.selectedDate.split('-').map(Number);
    state.month = new Date(year, month - 1, 1);
    renderScreen();
});

app.addEventListener('change', event => {
    if (event.target.matches('#available-only')) {
        const date = document.querySelector('#search-date')?.value;
        const time = document.querySelector('#search-time')?.value;
        if (event.target.checked && (!date || !time)) {
            event.target.checked = false;
            showToast('Informe data e horário e busque novamente para filtrar a disponibilidade.', true);
            return;
        }
        drawDoctorResults();
    }
});

app.addEventListener('submit', async event => {
    const form = event.target;
    if (!form.matches('form')) return;
    event.preventDefault();
    if (form.id === 'login-form') {
        const submit = form.querySelector('[type="submit"]');
        const values = Object.fromEntries(new FormData(form));
        submit.disabled = true;
        submit.textContent = 'Entrando...';
        try {
            const user = await api('/api/auth/login', { method: 'POST', body: JSON.stringify(values) });
            state.user = user;
            state.screen = 'overview';
            csrfState.token = null;
            await refreshCsrf();
            await loadPortal();
        } catch (error) {
            renderAuthForm('login', error.message);
        }
    } else if (form.id === 'register-form') {
        const submit = form.querySelector('[type="submit"]');
        const values = Object.fromEntries(new FormData(form));
        submit.disabled = true;
        submit.textContent = 'Criando conta...';
        try {
            state.user = await api('/api/auth/register', { method: 'POST', body: JSON.stringify(values) });
            state.screen = 'overview';
            csrfState.token = null;
            await refreshCsrf();
            await loadPortal();
        } catch (error) {
            renderAuthForm('register', error.message);
        }
    } else if (form.id === 'doctor-search') {
        await searchDoctors(event);
    } else if (form.id === 'profile-form') {
        const submit = form.querySelector('[type="submit"]');
        const error = document.querySelector('#profile-error');
        const values = Object.fromEntries(new FormData(form));
        if (!values.newPassword) delete values.newPassword;
        submit.disabled = true;
        error.hidden = true;
        try {
            const url = accountIsClient() ? '/api/cliente/perfil' : '/api/doutor/perfil';
            state.profile = await api(url, { method: 'PUT', body: JSON.stringify(values) });
            await api('/api/auth/me').then(user => { state.user = user; });
            showToast('Seus dados foram atualizados.');
            renderShell();
        } catch (requestError) {
            error.textContent = requestError.message;
            error.hidden = false;
        } finally { submit.disabled = false; }
    } else if (form.id === 'booking-form') {
        const submit = form.querySelector('[type="submit"]');
        const error = document.querySelector('#booking-error');
        const values = Object.fromEntries(new FormData(form));
        values.doctorId = Number(form.closest('dialog').dataset.doctorId);
        if (values.hora?.length === 5) values.hora += ':00';
        submit.disabled = true;
        try {
            await api('/api/cliente/agendamentos', { method: 'POST', body: JSON.stringify(values) });
            form.closest('dialog').close();
            await loadPortal();
            showToast('Agendamento confirmado.');
        } catch (requestError) {
            error.textContent = requestError.message;
            error.hidden = false;
            submit.disabled = false;
        }
    }
});

async function start() {
    try {
        await refreshCsrf();
        state.user = await api('/api/auth/me');
        state.screen = 'overview';
        await loadPortal();
    } catch {
        renderAuth();
    }
}

start();
