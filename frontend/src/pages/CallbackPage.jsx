// import React, { useEffect } from "react";
// import { useNavigate } from "react-router-dom";

// const CallbackPage = () => {
//   const navigate = useNavigate();

//   useEffect(() => {
//     const params = new URLSearchParams(window.location.search);
//     const authCode = params.get("auth_code");

//     if (authCode) {
//       fetch(`http://127.0.0.1:8000/api/social/google/exchange/?auth_code=${authCode}`)
//         .then((res) => res.json())
//         .then((data) => {
//           if (data.access && data.refresh) {
//             localStorage.setItem("access", data.access);
//             localStorage.setItem("refresh", data.refresh);
//             localStorage.setItem("email", data.email);

                     
//             // const authData = {
//             //   access: data.access,
//             //   refresh: data.refresh,
//             // };

//             // // ✅ save tokens in Redux + localStorage
//             // dispatch(setTokens(authData));


//             navigate("/dashboard"); // ✅ redirect to your app
//           } else {
//             console.error("Invalid response:", data);
//           }
//         })
//         .catch((err) => console.error("Error fetching tokens:", err));
//     }
//   }, [navigate]);

//   return (
//     <div>
//       <h1>Logging you in...</h1>
//     </div>
//   );
// };

// export default CallbackPage;




import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { setTokens, logout } from "../features/auth/authSlice";

const CallbackPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const handleAuth = async () => {
      try {
        const params = new URLSearchParams(window.location.search);
        const authCode = params.get("auth_code");

        if (!authCode) return;

        const res = await fetch(
          `http://127.0.0.1:8000/api/social/google/exchange/?auth_code=${authCode}`
        );

        const data = await res.json();

        if (res.ok && data.access && data.refresh) {
          // If you’re not yet using Redux:
          // localStorage.setItem("access", data.access);
          // localStorage.setItem("refresh", data.refresh);
          // localStorage.setItem("email", data.email);

          // ✅ Redux version (once ready)
          dispatch(setTokens({ access: data.access, refresh: data.refresh , email: data.email }));
          
          // navigate("/dashboard") 
          {!data.created?navigate("/dashboard") : navigate("/onboarding");}
        } else {
          console.error("Invalid response:", data);
          dispatch(logout()); // if you want to reset Redux state
        }
      } catch (err) {
        console.error("Error fetching tokens:", err);
        dispatch(logout());
      }
    };

    handleAuth();
  }, [navigate]);

  return (
    <div>
      <h1>Logging you in...</h1>
    </div>
  );
};

export default CallbackPage;
