from django.contrib.auth import get_user_model
from rest_framework.serializers import ModelSerializer

from crm.models import *

from rest_framework import serializers


UserModel = get_user_model()

class UserRegistrationSerializer(ModelSerializer):
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True, min_length=8)
    first_name = serializers.CharField(required=False, allow_blank=True, max_length=150)
    last_name = serializers.CharField(required=False, allow_blank=True, max_length=150)

    class Meta:
        model = UserModel
        fields = ['email', 'password', 'first_name', 'last_name']

    def validate_email(self, value):
        if UserModel.objects.filter(email=value).exists():
            raise serializers.ValidationError("A user with this email already exists.")
        return value

    def create(self, validated_data):
        user_obj = UserModel.objects.create_user(
            email=validated_data['email'],
            password=validated_data['password'],
            first_name=validated_data.get('first_name', ''),
            last_name=validated_data.get('last_name', ''),
        )
        return user_obj


class UserProfileSerializer(ModelSerializer):
    
    class Meta:
        model = UserProfile
        fields = '__all__'

