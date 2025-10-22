from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.contrib.auth import authenticate, get_user_model
from rest_framework_simplejwt.tokens import RefreshToken
import requests
from allauth.socialaccount.models import SocialAccount

# from .authentication import CookieJWTAuthentication

from django.conf import settings

from django.shortcuts import redirect

from rest_framework.permissions import IsAuthenticated , AllowAny

from django.core.cache import cache

import secrets

from rest_framework.decorators import api_view

from .serializers import UserRegistrationSerializer

User = get_user_model()


# # 🔹 Registration
# class RegisterView(APIView):
#     def post(self, request):
#         email = request.data.get("email")
#         password = request.data.get("password")
#         first_name = request.data.get("first_name", "")
#         last_name = request.data.get("last_name", "")

#         if not email or not password:
#             return Response({"detail": "Email and password required"}, status=status.HTTP_400_BAD_REQUEST)

#         if User.objects.filter(email=email).exists():
#             return Response({"detail": "User already exists"}, status=status.HTTP_400_BAD_REQUEST)

#         user = User.objects.create_user(
#             email=email,
#             password=password,
#             first_name=first_name,
#             last_name=last_name,
#         )
#         return Response({"detail": "User registered successfully"}, status=status.HTTP_201_CREATED)



class RegisterView(APIView):
    def post(self, request):
        serializer = UserRegistrationSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response({"detail": "User registered successfully"}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

# 🔹 Login
class LoginView(APIView):
    def post(self, request):
        email = request.data.get("email")
        password = request.data.get("password")

        user = authenticate(request, email=email, password=password)
        if user is None:
            return Response({"detail": "Invalid credentials"}, status=status.HTTP_401_UNAUTHORIZED)

        refresh = RefreshToken.for_user(user)

        response = Response({
            # "refresh": str(refresh),
            "access": str(refresh.access_token),
            "email": user.email,
        })

        response.set_cookie(
            key="refresh_token",
            value=str(refresh),
            httponly=True,
            secure=True,   # only over HTTPS
            samesite="Strict",
        )
        return response




# 🔹 Google Social Login
class GoogleLoginView(APIView):
   permission_classes = [AllowAny]


   def get(self, request):
        code = request.query_params.get("code")
        if not code:
            return Response({"error": "Missing code"}, status=status.HTTP_400_BAD_REQUEST)

        # Exchange code for tokens
        token_url = "https://oauth2.googleapis.com/token"

        client_id= settings.GOOGLE_CLIENT_ID
        client_secret= settings.GOOGLE_SECRET  # only backend knows this
        redirect_uri = settings.GOOGLE_REDIRECT_URI
        grant_type= "authorization_code"
        code_verifier = settings.GOOGLE_CODE_VERIFIER
        data = {
            "code": code,
            "client_id": client_id,
            "client_secret": client_secret,  # only backend knows this
            "redirect_uri": "http://127.0.0.1:8000/api/social/google/",
            "grant_type": "authorization_code",
            "code_verifier": code_verifier,  # same one used to make challenge
        }

        r = requests.post(token_url, data=data)
        token_response = r.json()
        access_token = token_response['access_token']

        if not access_token:
            return Response({"detail": "No access_token provided"}, status=status.HTTP_400_BAD_REQUEST)

        # Use the access token to get user info
        google_url = "https://www.googleapis.com/oauth2/v3/userinfo"
        r = requests.get(google_url, headers={"Authorization": f"Bearer {access_token}"})

        if r.status_code != 200:
            return Response({"detail": "Invalid Google token"}, status=status.HTTP_400_BAD_REQUEST)

        data = r.json()
        email = data.get("email")
        if not email:
            return Response({"detail": "Google token invalid: no email"}, status=status.HTTP_400_BAD_REQUEST)

        user, created = User.objects.get_or_create(email=email)
        if created:
            user.first_name = data.get("given_name", "")
            user.last_name = data.get("family_name", "")
            user.set_unusable_password()
            user.save()

            SocialAccount.objects.get_or_create(user=user, provider="google", uid=data["sub"])
            
            
            # ---- Create JWT tokens ----
        
        refresh = RefreshToken.for_user(user)
        tokens = {
            "access": str(refresh.access_token),
            "refresh": str(refresh),
            "email": email,
            "created": created,
        }

        # print(email)
        # print(tokens)

        # ---- Store tokens in server cache for 1 minute ----
        auth_code = secrets.token_urlsafe(32)
        cache.set(auth_code, tokens, timeout=60)

        # ---- Redirect frontend with ONLY auth_code ----
        frontend_url = f"{settings.FRONTEND_CALLBACK_URL}?auth_code={auth_code}"
        return redirect(frontend_url)
        # refresh = RefreshToken.for_user(user)

        # response = Response({
        #     # "refresh": str(refresh),
        #     "access": str(refresh.access_token),
        #     "refresh" : str(refresh),
        #     "email": user.email,
        # })

        # return response

        # You get access_token + id_token + refresh_token
        # Verify id_token or call Google UserInfo API
        # return Response(token_response, status=status.HTTP_200_OK)


@api_view(["GET"])
def exchange_code(request):
    auth_code = request.query_params.get("auth_code")
    if not auth_code:
        return Response({"error": "Missing auth_code"}, status=400)

    tokens = cache.get(auth_code)
    print(tokens)
    if not tokens:
        return Response({"error": "Invalid or expired auth_code"}, status=400)

    # one-time use only
    # cache.delete(auth_code)

    return Response(tokens)

class MeView(APIView):
    # permission_classes = [IsAuthenticated]

    def get(self, request):
        user = request.user
        return Response({
            "id": user.id,
            "email": user.email,
            "first_name": user.first_name,
            "last_name": user.last_name,
        })