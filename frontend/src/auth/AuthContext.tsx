import { createContext, useContext, useSyncExternalStore } from 'react';
import type { PropsWithChildren } from 'react';
import { api } from '../api/client';
import type { LoginInput, RegisterInput, Session } from '../api/types';

interface AuthContextValue {
  session: Session | null;
  login: (input: LoginInput) => Promise<void>;
  register: (input: RegisterInput) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: PropsWithChildren) {
  const session = useSyncExternalStore(api.subscribe, api.getSession);
  return (
    <AuthContext.Provider value={{
      session,
      login: (input) => api.login(input),
      register: (input) => api.register(input),
      logout: api.logout,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const value = useContext(AuthContext);
  if (!value) throw new Error('AuthProvider is required');
  return value;
}
