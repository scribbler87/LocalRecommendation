from django import forms

class RegisterForm(forms.Form):
	name = forms.CharField(max_length = 64)
	pass1 = forms.CharField(widget=forms.PasswordInput)
	pass2 = forms.CharField(widget=forms.PasswordInput)
	mail = forms.CharField(max_length = 256)