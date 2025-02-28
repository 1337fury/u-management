"use client";

import {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import { authAPI, userAPI } from "@/services/api";
import { useRouter } from "next/navigation";

interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const token = document.cookie
      .split("; ")
      .find((row) => row.startsWith("token="))
      ?.split("=")[1];
    console.log("token:", token);
    if (token) {
      fetchUserProfile();
    } else {
      setIsLoading(false);
      router.push("/login");
    }
  }, []);

  const fetchUserProfile = async () => {
    try {
      const userData = await userAPI.getMyProfile();
      setUser(userData);
    } catch (error) {
      console.error("Failed to fetch user profile:", error);
      document.cookie = "token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
    } finally {
      setIsLoading(false);
    }
  };

  const login = async (username: string, password: string) => {
    setIsLoading(true);
    try {
      const response = await authAPI.login(username, password);
      document.cookie = `token=${response.accessToken}; path=/;`;
      await fetchUserProfile();
      router.push("/dashboard");
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    document.cookie = "token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
    setUser(null);
    router.push("/login");
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: !!user,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
