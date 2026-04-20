export interface AuthLoginRequest {
  username: string;
  password: string;
}

export interface AuthApiResponse {
  token: string;
  type: string;
  username: string;
  roles: string[];
}

export interface AuthSession {
  token: string;
  username: string;
  roles: string[];
}
