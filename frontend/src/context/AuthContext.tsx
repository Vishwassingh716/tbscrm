import { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();
export default AuthContext;

export const AuthProvider = ({ children }) => {
  let [authToken, setAuthToken] = useState(() =>
    localStorage.getItem("authToken")
      ? JSON.parse(localStorage.getItem("authToken"))
      : null
  );

  let [user, setUser] = useState(() =>
    localStorage.getItem("authToken")
      ? jwtDecode(JSON.parse(localStorage.getItem("authToken")).access)
      : null
  );

  const navigate = useNavigate();

  // 🔄 Refresh token function
  const updateToken = async () => {
    if (!authToken?.refresh) {
      logoutUser();
      return;
    }

    try {
      let response = await fetch("http://localhost:8000/api/token/refresh/", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ refresh: authToken.refresh }),
      });

      let data = await response.json();

      if (response.status === 200) {
        let newAuthToken = {
          access: data.access,
          refresh: authToken.refresh, // keep same refresh token
        };

        setAuthToken(newAuthToken);
        setUser(jwtDecode(data.access));
        localStorage.setItem("authToken", JSON.stringify(newAuthToken));
      } else {
        logoutUser();
      }
    } catch (error) {
      console.error("Failed to refresh token:", error);
      logoutUser();
    }
  };

  useEffect(() => {
    if (authToken) {
      const decoded = jwtDecode(authToken.access);
      if (decoded.exp * 1000 < Date.now()) {
        updateToken();
      } else {
        setUser(decoded);
      }
    }
  }, [authToken]);

  // ⏱️ Auto refresh token every 4 minutes
  useEffect(() => {
    let interval = setInterval(() => {
      if (authToken) {
        updateToken();
      }
    }, 1000 * 60 * 4); // 4 minutes

    return () => clearInterval(interval);
  }, [authToken]);

  let registerUser = async (e) => {
    e.preventDefault();
    let response = await fetch("http://localhost:8000/api/register/", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: e.target.email.value,
        password: e.target.password.value,
      }),
    });

    if (response.status === 201) {
      navigate("/login");
      alert("your account is created");
    }
  };

  let loginUser = async (e) => {
    e.preventDefault();
    let response = await fetch("http://localhost:8000/api/login/", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: e.target.email.value,
        password: e.target.password.value,
      }),
    });

    let data = await response.json();
    if (response.status === 200) {
      setAuthToken(data);
      setUser(jwtDecode(data.access));
      localStorage.setItem("authToken", JSON.stringify(data));
      navigate("/");
    } else {
      alert("something went wrong");
    }
  };

  let logoutUser = async (e) => {
    if (e && e.preventDefault) e.preventDefault();
    localStorage.removeItem("authToken");
    setAuthToken(null);
    setUser(null);
    navigate("/login");
  };

  let contextData = {
    user: user,
    loginUser: loginUser,
    authToken: authToken,
    logoutUser: logoutUser,
    registerUser: registerUser,
  };

  return (
    <AuthContext.Provider value={contextData}>
      {children}
    </AuthContext.Provider>
  );
};
