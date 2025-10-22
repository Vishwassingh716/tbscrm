import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Provider } from "react-redux";
import {store} from "./app/store"; // your redux store/

// pages
import LoginPage from "./pages/LoginPage";
import CallbackPage from "./pages/CallbackPage";
import DashBoardPage from "./pages/DashBoardPage";
import HomePage from "./pages/HomePage";

// components
import PrivateRoute from "./components/PrivateRoute";
// import FirstTimeLogin from "./pages/FirstTimeLogin";

import OnboardingPage from "./pages/OnboardingPage";
import RegisterPage from "./pages/RegisterPage";





export default function App() {
  return (
    <Provider store={store}>
      <Router>
        <Routes>
          {/* Public routes */}
         
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/auth/callback" element={<CallbackPage />} />

          {/* Protected routes */}
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <DashBoardPage />
              </PrivateRoute> 
            }
          />
          <Route
            path="/onboarding"
            element={
              <PrivateRoute>
                <OnboardingPage/>
              </PrivateRoute> 
            }
          />
          <Route
            path="/"
            element={
              <PrivateRoute>
                <HomePage />
              </PrivateRoute> 
            }
          />
        </Routes>
      </Router>
    </Provider>
  );
}






// import { useState } from 'react'
// import { Box , Typography } from '@mui/material'

// import LoginPage from './pages/LoginPage'
// import './App.css'

// function App() {


//   return (
//     <Box>
//       <LoginPage/>
//     </Box>
//   )
// }

// export default App
