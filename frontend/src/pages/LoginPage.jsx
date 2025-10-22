import React , { useState, useEffect } from 'react'
import { Box , Button, Input , TextField , Typography } from '@mui/material'

// images
import logincover from "../assets/logincover.jpg";
import venuecover from "../assets/venuecover.jpg";

// animation




const words = ['The Bride Side', 'Decor', 'Photography', 'Venue' , 'Make-up'];


// icons
import EmailOutlinedIcon from '@mui/icons-material/EmailOutlined';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { FcGoogle } from "react-icons/fc";
import InstagramIcon from '@mui/icons-material/Instagram';
import FacebookIcon from '@mui/icons-material/Facebook';
import LinkedInIcon from '@mui/icons-material/LinkedIn';

// src/utils/googleAuth.js
import { clientId, redirectUri, scope } from "../config";

import { useDispatch, useSelector } from "react-redux";
import { loginUser } from "../features/auth/authSlice";
import { Navigate } from "react-router-dom";

function generateCodeVerifier(length = 128) {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
  let result = "";
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  // localStorage.setItem("code_verifier", result); // save for later
  // return result;
  return "NrE2f0dtSVG_ecllinTeJuC8-KRGFDvbSkwwNlnts_o7jTPF5UxXSb5XrRTMwhmEf5wWyoevRGg4t8rJ6uQaIQ";
}

async function generateCodeChallenge(verifier) {
  const encoder = new TextEncoder();
  const data = encoder.encode(verifier);
  const digest = await crypto.subtle.digest("SHA-256", data);
  // return btoa(String.fromCharCode(...new Uint8Array(digest)))
  //   .replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
  return "a2DLR71CIRh-nEaQokKTcmT1RaDAUF0MiyA3b0TCS0A"
}

const LoginPage = () => {

  // animation 
  const [text, setText] = useState('');
  const [wordIndex, setWordIndex] = useState(0);
  const [charIndex, setCharIndex] = useState(0);
  const [typing, setTyping] = useState(true);

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (typing) {
        if (charIndex < words[wordIndex].length) {
          setText((prev) => prev + words[wordIndex][charIndex]);
          setCharIndex(charIndex + 1);
        } else {
          setTyping(false); // start deleting
        }
      } else {
        if (charIndex > 0) {
          setText((prev) => prev.slice(0, -1));
          setCharIndex(charIndex - 1);
        } else {
          setTyping(true); // move to next word
          setWordIndex((wordIndex + 1) % words.length);
        }
      }
    }, typing ? 100 : 100);

    return () => clearTimeout(timeout);
  }, [charIndex, typing, wordIndex]);
// 

  const handleLogin = async () => {
    const verifier = generateCodeVerifier();
    const challenge = await generateCodeChallenge(verifier);

    const clientId ="635295883014-pseb19fa4snvrvhcd5fqbr7bm62shjbv.apps.googleusercontent.com";
    const redirectUrl = redirectUri; // 👈 React route
    const scope = "openid email profile";

    const url =
      `https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?client_id=635295883014-pseb19fa4snvrvhcd5fqbr7bm62shjbv.apps.googleusercontent.com&redirect_uri=http%3A%2F%2F127.0.0.1%3A8000%2Fapi%2Fsocial%2Fgoogle%2F&response_type=code&scope=openid%20email%20profile&code_challenge=a2DLR71CIRh-nEaQokKTcmT1RaDAUF0MiyA3b0TCS0A&code_challenge_method=S256&service=lso&o2v=2&flowName=GeneralOAuthFlow`;

    window.location.href = url;
  };

  const dispatch = useDispatch();
  const { authToken, loading, error } = useSelector((state) => state.auth);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  // 🔹 if user already logged in, redirect
  if (authToken?.access) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("runninggggggggggggggggggggggggg");
    
    dispatch(loginUser({ email, password }));
  };
  return (
    <Box
     sx={{
        backgroundColor: "#f5f5f5",
        overflow: "hidden"
      }}
    >
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            minHeight: "100vh",
            backgroundColor: "#f5f5f5",
            width:'100%',
            mx:10,
            overflow:"hidden",
          }}
        >
        <Box>
      <Box
        sx={{
          display: "flex",
          flexDirection:'column',
          justifyContent: "center",
          alignItems: "center",
          width: "350px",
          p: 4,
          border: "1px solid #ddd",
          borderRadius: 3,
          backgroundColor: "white",
          boxShadow: 3,
        }}
      >


        <Typography
          variant="h4"
          sx={{
            
            
            mb: 3,
            fontFamily: "'Great Vibes', cursive",
            fontWeight: "bold",
            color: "#333",
          }}
        >
          The Bride Side
        </Typography>
        <form>
        <TextField 
          type="text" 
          name="email" 
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          label={
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <EmailOutlinedIcon fontSize="small" />
              <span>Email</span>
            </Box>
          }

           
          // variant="filled" 
          fullWidth 
          required 
          InputLabelProps={{ required: false }} 
          sx={{ marginTop: 2,
            backgroundColor: 'white',
            borderRadius: 50
           }}
        />
        <TextField 
          type="password" 
          name="password" 
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          label={
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <LockOutlinedIcon fontSize="small" />
              <span>Password</span>
            </Box>
          }

          InputLabelProps={{ required: false }} 
          // variant="filled" 
          fullWidth 
          required 
          sx={{ marginTop: 2,
            backgroundColor: 'white',
            borderRadius: 50
           }}
        />
              {error?(<Typography sx = {{ justifyItems:'center', color:'red'}}> invalid email or password
          </Typography>):null}
        <Button 
          onClick={handleSubmit}
          type="submit" 
          variant="contained" 
          color="primary" 
          fullWidth
          sx={{ marginTop: 5,
            
            color: '#FFB6C1',
            borderRadius: 20,
            backgroundColor: 'green',
           }}
        >
          Sign in
        </Button>
      </form>

          
      <Button 
          onClick={handleLogin}
          type="submit" 
          variant="contained" 
          color="primary" 
          fullWidth
          startIcon={<FcGoogle />}
          sx={{ marginTop: 2,

            fontFamily: "Poppins, sans-serif", 
            fontWeight: 600, 
            // color: "black" ,
            border: '1px solid grey',
            color: 'green',
            borderRadius: 20,
            backgroundColor: 'white',
           }}
        >
          Google
        </Button>
      </Box>
      <Box sx = {{display:'flex', flexDirection:'row' , justifyContent:'space-between'}}>
        <Typography sx = {{margin: 2 ,color:'grey.600' , cursor: "pointer" }}>Don't have an account? </Typography>
        <Typography sx = {{margin: 2 ,color:'grey.600', cursor: "pointer" }}>forgot password? </Typography>
      </Box>

     
      </Box>
          <Box
          sx={{
            marginLeft : 5 ,
            height: "100vh", // full screen
            width :'100%',
            display: "flex",
            justifyContent: "center",
             position: "relative",
            // flexDirection:'row',
            // alignItems: "center",
            // backgroundImage: "url('/images/logincover.jpg')", // 👈 put image in /public/images
            backgroundImage: `url(${venuecover})` ,
            backgroundSize: "cover",
            // backgroundPosition: "center",
          }}
    >
      <Box sx = {{display:'flex' , flexDirection : 'column' , justifyContent:'space-between'}}>
      <Typography variant="h4" color="primary" sx={{ marginTop : '10px' ,
                                                    fontFamily: 'monospace',
                                                    color: 'white',
                                                    textShadow: '2px 2px 4px rgba(0,0,0,0.6)',
                                                    fontSize: '3rem',}}>
          {text}
          <span style={{
            display: 'inline-block',
            width: '1ch',
            backgroundColor: 'currentColor',
            marginLeft: '2px',
            animation: 'blink 1s steps(2, start) infinite'
          }}></span>

          <style>
            {`
              @keyframes blink {
                0%, 50%, 100% { opacity: 1; }
                25%, 75% { opacity: 0; }
              }
            `}
          </style>
    </Typography>
      <Box
      sx = {{
        display:'flex',
        flexDirection:'row' ,
        position: "absolute",
        bottom: 20,
        left: "50%",
        transform: "translateX(-50%)",
        gap: 2,

      }}>
        <a
        href="https://www.instagram.com/thebrideside.in"
        target="_blank"
        rel="noopener noreferrer"><InstagramIcon sx={{ color: "grey.700", cursor: "pointer" }} /></a>

        <a
        href="https://www.facebook.com/yourprofile"
        target="_blank"
        rel="noopener noreferrer"><FacebookIcon sx={{ color: "grey.700", cursor: "pointer" }} /></a>
        <a
        href="https://www.linkedin.com/company/thebrideside/posts/?feedView=all"
        target="_blank"
        rel="noopener noreferrer"><LinkedInIcon sx={{ color: "grey.700", cursor: "pointer" }} /></a>
      </Box>
    </Box>
    </Box>
      </Box>
    </Box>
  )
}

export default LoginPage
