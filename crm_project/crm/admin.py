from django.contrib import admin

# Register your models here.
from .models import *

admin.site.register(User)
admin.site.register(UserProfile)
admin.site.register(Company)
class CompanyAdmin(admin.ModelAdmin):
    list_display = ('name', 'code', 'company_type', 'company_size')