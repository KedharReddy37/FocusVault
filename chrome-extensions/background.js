// ── Constants ──────────────────────────────────────────
const API_BASE_URL = 'http://localhost:8081';
const EVENTS_ENDPOINT = `${API_BASE_URL}/api/events`;
const LOGIN_ENDPOINT = `${API_BASE_URL}/api/auth/login`;

// ── State ───────────────────────────────────────────────
let currentDomain = null;
let currentStartTime = null;
let authToken = null;

// ── Category mapping ────────────────────────────────────
const DOMAIN_CATEGORIES = {
  'youtube.com': 'ENTERTAINMENT',
  'netflix.com': 'ENTERTAINMENT',
  'primevideo.com': 'ENTERTAINMENT',
  'hotstar.com': 'ENTERTAINMENT',
  'instagram.com': 'SOCIAL',
  'twitter.com': 'SOCIAL',
  'x.com': 'SOCIAL',
  'facebook.com': 'SOCIAL',
  'linkedin.com': 'SOCIAL',
  'github.com': 'WORK',
  'stackoverflow.com': 'WORK',
  'gitlab.com': 'WORK',
  'jira.com': 'WORK',
  'notion.so': 'WORK',
  'udemy.com': 'EDUCATION',
  'coursera.org': 'EDUCATION',
  'medium.com': 'EDUCATION',
  'dev.to': 'EDUCATION',
  'amazon.in': 'SHOPPING',
  'flipkart.com': 'SHOPPING',
  'myntra.com': 'SHOPPING',
  'timesofindia.com': 'NEWS',
  'ndtv.com': 'NEWS',
  'reddit.com': 'OTHER',
};

function getCategory(domain) {
  for (const [key, category] of Object.entries(DOMAIN_CATEGORIES)) {
    if (domain.includes(key)) return category;
  }
  return 'OTHER';
}

function extractDomain(url) {
  try {
    const hostname = new URL(url).hostname;
    return hostname.replace('www.', '');
  } catch (e) {
    return null;
  }
}

function formatDateTime(date) {
  return date.toISOString().slice(0, 19);
}

// ── Load token from storage on startup ─────────────────
chrome.storage.local.get(['authToken'], (result) => {
  if (result.authToken) {
    authToken = result.authToken;
    console.log('FocusVault: Token loaded from storage');
  }
});

// ── Send browsing event to backend ─────────────────────
async function sendBrowsingEvent(domain, startTime, endTime) {
  if (!authToken) {
    console.log('FocusVault: No token, skipping event');
    return;
  }

  if (!domain || domain === 'newtab' || domain === 'extensions') {
    return;
  }

  const durationSeconds = (endTime - startTime) / 1000;
  if (durationSeconds < 5) {
    console.log('FocusVault: Too short, skipping');
    return;
  }

  const event = {
    domain: domain,
    startTime: formatDateTime(startTime),
    endTime: formatDateTime(endTime),
    category: getCategory(domain)
  };

  console.log('FocusVault: Sending event:', event);

  try {
    const response = await fetch(EVENTS_ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authToken}`
      },
      body: JSON.stringify(event)
    });

    if (response.ok) {
      console.log('FocusVault: Event sent successfully');
    } else if (response.status === 401 || response.status === 403) {
      console.log('FocusVault: Token expired, clearing');
      authToken = null;
      chrome.storage.local.remove(['authToken']);
    } else {
      console.log('FocusVault: Error sending event:', response.status);
    }
  } catch (error) {
    console.log('FocusVault: Network error:', error.message);
  }
}

// ── Handle tab changes ──────────────────────────────────
function handleTabChange(newUrl) {
  const newDomain = extractDomain(newUrl);
  const now = new Date();

  // Send event for previous domain
  if (currentDomain && currentStartTime) {
    sendBrowsingEvent(currentDomain, currentStartTime, now);
  }

  // Start tracking new domain
  currentDomain = newDomain;
  currentStartTime = now;

  console.log('FocusVault: Now tracking:', newDomain);
}

// ── Listen for tab activation ───────────────────────────
chrome.tabs.onActivated.addListener((activeInfo) => {
  chrome.tabs.get(activeInfo.tabId, (tab) => {
    if (tab && tab.url) {
      handleTabChange(tab.url);
    }
  });
});

// ── Listen for tab URL changes ──────────────────────────
chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
  if (changeInfo.status === 'complete' && tab.active && tab.url) {
    handleTabChange(tab.url);
  }
});

// ── Listen for window focus changes ────────────────────
chrome.windows.onFocusChanged.addListener((windowId) => {
  if (windowId === chrome.windows.WINDOW_ID_NONE) {
    // Browser lost focus — send current event
    if (currentDomain && currentStartTime) {
      sendBrowsingEvent(currentDomain, currentStartTime, new Date());
      currentStartTime = new Date();
    }
  }
});

// ── Login function (called from popup) ─────────────────
async function login(email, password) {
  try {
    const response = await fetch(LOGIN_ENDPOINT, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (response.ok) {
      const data = await response.json();
      if (data && data.token) {
    authToken = data.token;
    chrome.storage.local.set({ authToken: data.token });

      console.log('FocusVault: Logged in successfully');
      return { success: true };
      }
    } else {
      return { success: false, message: 'Invalid credentials' };
    }
  } catch (error) {
    return { success: false, message: 'Cannot connect to server' };
  }
}

// ── Message listener (from popup) ──────────────────────
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === 'LOGIN') {
    login(message.email, message.password).then(sendResponse);
    return true; // keeps message channel open for async
  }

  if (message.type === 'LOGOUT') {
    authToken = null;
    currentDomain = null;
    currentStartTime = null;
    chrome.storage.local.remove(['authToken']);
    sendResponse({ success: true });
  }

  if (message.type === 'GET_STATUS') {
    sendResponse({
      isLoggedIn: !!authToken,
      currentDomain: currentDomain,
      tracking: !!currentDomain
    });
  }
});

console.log('FocusVault: Background service started');