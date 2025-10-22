import React from "react";
import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

export default function PrivateRoute({ children }) {
  const { authToken } = useSelector((state) => state.auth);
  console.log("ACCESS TOKEN FROM REDUX:", authToken);
  if (!authToken) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
