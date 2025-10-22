from django.urls import path , include

from crm.views import * 

from rest_framework_simplejwt.views import (
    TokenRefreshView,
)

urlpatterns = [
    path("register/", RegisterView.as_view(), name="register"),
    path("login/", LoginView.as_view(), name="login"),
    path("social/google/", GoogleLoginView.as_view(), name="google-login"),
    path("social/google/exchange/", exchange_code, name="google-exchange"),
    path("me/", MeView.as_view(), name="me"),
    path("token/refresh/", TokenRefreshView.as_view(), name="token_refresh"),
]
