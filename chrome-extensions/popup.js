// ── Get DOM elements ──────────────────────────────
const loggedInView = document.getElementById('logged-in-view');
const loggedOutView = document.getElementById('logged-out-view');
const loadingView = document.getElementById('loading-view');
const currentDomainEl = document.getElementById('current-domain');
const loginBtn = document.getElementById('login-btn');
const logoutBtn = document.getElementById('logout-btn');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const errorMsg = document.getElementById('error-msg');

// ── Check status on popup open ────────────────────
chrome.runtime.sendMessage({ type: 'GET_STATUS' }, (response) => {
  loadingView.style.display = 'none';

  if (response && response.isLoggedIn) {
    loggedInView.style.display = 'block';
    currentDomainEl.textContent = response.currentDomain || 'Unknown';
  } else {
    loggedOutView.style.display = 'block';
  }
});

// ── Login ─────────────────────────────────────────
loginBtn.addEventListener('click', () => {
  const email = emailInput.value.trim();
  const password = passwordInput.value.trim();

  if (!email || !password) {
    errorMsg.textContent = 'Please enter email and password';
    return;
  }

  loginBtn.textContent = 'Logging in...';
  loginBtn.disabled = true;
  errorMsg.textContent = '';

  chrome.runtime.sendMessage(
    { type: 'LOGIN', email, password },
    (response) => {
      if (response && response.success) {
        loggedOutView.style.display = 'none';
        loggedInView.style.display = 'block';
      } else {
        errorMsg.textContent = response?.message || 'Login failed';
        loginBtn.textContent = 'Login & Start Tracking';
        loginBtn.disabled = false;
      }
    }
  );
});

// ── Logout ────────────────────────────────────────
logoutBtn.addEventListener('click', () => {
  chrome.runtime.sendMessage({ type: 'LOGOUT' }, () => {
    loggedInView.style.display = 'none';
    loggedOutView.style.display = 'block';
  });
});

// ── Allow Enter key to login ──────────────────────
passwordInput.addEventListener('keypress', (e) => {
  if (e.key === 'Enter') loginBtn.click();
});