from django.conf.urls.defaults import patterns, include, url
from django.contrib import admin
admin.autodiscover()

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    (r'^admin/', include(admin.site.urls)),
    (r'^login$', 'django.contrib.auth.views.login'),
    (r'^about$', 'deepint.project.views.about'),
    (r'^mobilelogin$', 'deepint.project.views.mobilelogin'),
    (r'^canvas/$', 'deepint.project.views.canvas'),
    (r'^logout$', 'django.contrib.auth.views.logout'),
    (r'^location$', 'deepint.project.views.location'),
    (r'^statics/(?P<path>.*)$', 'django.views.static.serve',
       {'document_root' : 'statics/'}),
    (r'^$', 'deepint.project.views.main'),
    (r'^users/(?P<userTarget>.*)$','deepint.project.views.showUser'),
    (r'^post/create$', 'deepint.project.views.createPost'),
    (r'^post/(?P<userTarget>.*)/(?P<postName>.*)$','deepint.project.views.showPost'),
    (r'^post/(?P<userTarget>.*)$','deepint.project.views.showUserPosts'),
    url(r'', include('social_auth.urls')),

)
