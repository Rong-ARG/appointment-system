const API_BASE_URL = "http://localhost:8080";
const TOKEN_KEY = "appointment_system_token";

function saveToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

function isLoggedIn() {
  return !!getToken();
}

async function apiFetch(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });

  let body = null;
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    body = await response.json().catch(() => null);
  }

  if (!response.ok) {
    const message = (body && (body.message || body.error)) || `Error ${response.status}`;
    throw new Error(message);
  }

  return body;
}

function requireAuth() {
  if (!isLoggedIn()) {
    window.location.href = "login.html";
  }
}

function logout() {
  clearToken();
  window.location.href = "login.html";
}

function showStatus(el, message, type) {
  el.textContent = message;
  el.className = `status-msg show ${type}`;
}

function decodeToken() {
  const token = getToken();
  if (!token) return null;
  try {
    const payload = token.split(".")[1];
    const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(json);
  } catch {
    return null;
  }
}

async function getMyUserId() {
  const payload = decodeToken();
  if (!payload || !payload.sub) {
    throw new Error("Could not read current user from token");
  }
  const user = await apiFetch(`/api/users/email/${encodeURIComponent(payload.sub)}`);
  return user.id;
}