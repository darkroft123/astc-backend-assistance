const ASSISTANCE_URL = process.env.ASSISTANCE_GRAPHQL_URL;

function normalizeValue(value) {
  if (Array.isArray(value)) {
    return value.map(normalizeValue);
  }

  if (value && typeof value === 'object') {
    return Object.fromEntries(
      Object.entries(value).map(([key, innerValue]) => [key, normalizeValue(innerValue)])
    );
  }

  if (typeof value === 'bigint') {
    return value.toString();
  }

  return value;
}

export async function callAssistance(query, variables = {}, token = '') {
  if (!ASSISTANCE_URL) {
    throw new Error('Falta ASSISTANCE_GRAPHQL_URL en el .env del subgraph');
  }

  const headers = {
    'Content-Type': 'application/json'
  };

  if (token) {
    headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
  }

  const response = await fetch(ASSISTANCE_URL, {
    method: 'POST',
    headers,
    body: JSON.stringify({
      query,
      variables: normalizeValue(variables)
    })
  });

  const payload = await response.json();

  if (!response.ok) {
    throw new Error(payload?.errors?.map((error) => error.message).join(' | ') || 'Error llamando a assistance');
  }

  if (payload.errors?.length) {
    throw new Error(payload.errors.map((error) => error.message).join(' | '));
  }

  return payload.data;
}
