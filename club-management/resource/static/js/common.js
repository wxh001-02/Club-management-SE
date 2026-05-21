function getUser() {
    const user = sessionStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

function checkLogin() {
    return getUser() !== null;
}

function logout() {
    fetch('/api/user/logout');
    sessionStorage.removeItem('user');
    window.location.href = '/static/login.html';
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatTime(timeStr) {
    if (!timeStr) return '';
    const d = new Date(timeStr);
    if (isNaN(d.getTime())) return timeStr;
    const pad = n => String(n).padStart(2, '0');
    return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) +
        ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes());
}
