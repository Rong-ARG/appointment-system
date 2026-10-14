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
  const token = getToken();
  if (!token) return false;

  const payload = decodeToken();
  if (!payload || !payload.exp) return false;

  const isExpired = Date.now() >= payload.exp * 1000;
  if (isExpired) {
    clearToken();
    return false;
  }
  return true;
}

async function apiFetch(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });

  if (response.status === 401 && token) {
    logout();
    throw new Error("Session expired, please log in again.");
  }

  let body = null;
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    body = await response.json().catch(() => null);
  }

  if (!response.ok) {
    const message = (body && (body.message || body.error)) || `Error ${response.status}`;
    const error = new Error(message);
    error.status = response.status;
    throw error;
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
  const user = await getMyUser();
  return user.id;
}

async function getMyUser() {
  return await apiFetch("/api/users/me");
}

async function getMyProfessional() {
  const payload = decodeToken();
  if (!payload || !payload.sub) {
    throw new Error("Could not read current user from token");
  }
  try {
    return await apiFetch(`/api/professionals/email/${encodeURIComponent(payload.sub)}`);
  } catch (err) {
    if (err.status === 404) return null;
    throw err;
  }
}