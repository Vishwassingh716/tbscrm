from django.db import models
from django.contrib.auth.models import AbstractBaseUser, PermissionsMixin, BaseUserManager
from django.utils.translation import gettext_lazy as _
from .managers import CustomUserManager
import random
import string


def generate_company_code():
    letters = ''.join(random.choices(string.ascii_uppercase, k=3))
    digits = ''.join(random.choices(string.digits, k=3))
    letters2 = ''.join(random.choices(string.ascii_uppercase, k=3))
    return f"{letters}-{digits}-{letters2}"

class User(AbstractBaseUser, PermissionsMixin):
    email = models.EmailField(unique=True)
    first_name = models.CharField(max_length=150, blank=True)
    last_name = models.CharField(max_length=150, blank=True)
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    date_joined = models.DateTimeField(auto_now_add=True)

    # attach manager
    objects = CustomUserManager()

    USERNAME_FIELD = "email"   # login with email
    REQUIRED_FIELDS = []       # no username required

    def __str__(self):
        return self.email
    


class UserProfile(models.Model):
    user = models.ForeignKey(User , on_delete= models.CASCADE , related_name='profile')

    def __str__(self):
        return self.user.email

# class Company(models.Model):
#     owner = models.ForeignKey(User , on_delete=models.CASCADE , related_name='company')
#     COMP_TYPE = [
#         ("technology","Technology"),
#         ("financial services","Financial Services"),
#         ("healthcare","Healthcare"),
#         ("manufacturing","Manufacturing"),
#         ("e commerce","E commerce"),
#         ("education","Education"),
#         ("real estate","Real Estate"),
#         ("marketing","Marketing"),
#         ("consulting","Consulting"),
#         ("transportation","Transportation"),
#         ("media","Media"),
#         ("non-profit","Non-profit"),
#     ] 


#     BUSINESS_MOD = [
#         ("b2b","B2B"),
#         ("b2c","B2C"),
#         ("marketplace/platform","Marketplace/Platform"),
#         ("agency/service provider","Agency/Service provider"),
#         ("agency/service provider","Agency/Service provider"),
#     ] 

#     COMP_SIZE = [
#         ('(1–10 employees)' , '(1–10 employees)'),
#         ('(11–50 employees)' , '(11–50 employees)'),
#         ('(51–200 employees)' , '(51–200 employees)'),
#         ('(200+ employees)' , '(200+ employees)'),
#     ]


#     name = models.CharField(max_length=200)
#     company_type = models.CharField(max_length=100 , choices=COMP_TYPE , blank=True, null = True)
#     company_size = models.CharField(max_length=100 , choices=COMP_SIZE , blank=True, null = True)
#     business_model = models.CharField(max_length=100 , choices=BUSINESS_MOD , blank=True, null = True)
#     code = models.CharField(max_length=12, unique=True, null = True)
    

#     def save(self, *args, **kwargs):
#         if not self.code:
#             self.code = generate_company_code()
#         super().save(*args, **kwargs)


#     def __str__(self):
#         return self.name
    

# class Roles(models.Model):
#     company = models.ForeignKey(Company , on_delete=models.CASCADE , related_name='roles')
#     name = models.CharField(max_length=100)
#     description = models.TextField(blank=True)

#     class Meta:
#         unique_together = ('company', 'name')

#     def __str__(self):
#         return self.name
    

# class UserinCompany(models.Model):
#     user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='memberships')
#     company = models.ForeignKey(Company, on_delete=models.CASCADE, related_name='memberships')
#     role = models.ForeignKey(Roles, on_delete=models.SET_NULL, null=True, blank=True, related_name='members')

#     class Meta:
#         unique_together = ('user', 'company')

#     def __str__(self):
#         return f"{self.user.email} → {self.company.name} [{self.role.name if self.role else 'No Role'}]"
    
# class Team(models.Model):
#     company = models.ForeignKey(Company , on_delete=models.CASCADE , related_name='team')
#     name = models.CharField(max_length=100)

#     class Meta:
#         unique_together = ('company' , 'name')

#     def __str__(self):
#         return self.name
    
# class TeamMember(models.Model):
#     team = models.ForeignKey(Team, on_delete=models.CASCADE, related_name='team_memberships')
#     membership = models.ForeignKey(UserinCompany, on_delete=models.CASCADE, related_name='team_memberships')

#     class Meta:
#         unique_together = ('team', 'membership')

#     def __str__(self):
#         return f"{self.membership.user.email} in {self.team.name}"
    

