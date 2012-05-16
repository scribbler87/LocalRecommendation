from django.contrib.auth.models import User,Group
from django.shortcuts import render_to_response
from django.http import HttpResponse,HttpResponseNotFound,HttpResponseRedirect
#from django.db import IntegrityError
#from django.template.loader import get_template
#from django.template import Context
from django.db import IntegrityError
import facebook
from deepint.project.models import Post
from social_auth.models import UserSocialAuth


#from deepint.project.RegisterForm import RegisterForm

#from django.template import RequestContext

from datetime import *

from django.views.decorators.csrf import csrf_exempt


host = "http://group14.naf.cs.hut.fi/"
#uaAPP= "mozilla/5.0 (x11; ubuntu; linux"
uaAPP= "dalvik"
#dalvik
def create_fb_user(accesstoken,expires,id,first_name,last_name):



    try:
                user = User.objects.create_user(id, '', '')

    except IntegrityError:
                explan = 'The user is already in our database'
                return render_to_response('info.xml', {"explan":explan})
    

    #useri = User.objects.get(username__exact='YoYuUm')
    user.first_name=first_name
    user.last_name=last_name
    user.save()

    fuser = UserSocialAuth(user = user,
                            provider = 'facebook',
                            uid = id,
                            extra_data = '{"access_token": "'+ accesstoken + '", "expires": "'+expires+'", "id": "'+id+'"}'
                            );
    fuser.save();

def checkuser(accesstoken, id):
    
    try:
    	user = User.objects.get(username__exact=id)
		
	
    except User.DoesNotExist:
	
	explan = 'User does not exist in our DB'
        return render_to_response ('info' + '.xml', {"explan":explan}, mimetype="application/xml")
	
    
    userAT = user.social_auth.get().extra_data['access_token']
    if userAT != accesstoken:
	user = None
    return user

def app(request):
    vapp = False
    ua= request.META['HTTP_USER_AGENT'].lower()[0:6]#0:31
    print ua
    if ((ua == uaAPP) or (ua == "apache")):
        print ('son iguales')
        vapp = True
    else:
        print ('son distintos')
    return vapp

def main(request):
    
    vapp = app(request)
    

    latest = Post.objects.order_by('-date')[:30]
    

    if request.user.is_authenticated():
        user = request.user
        
    else:
        user = None
    if vapp:
        return render_to_response ('main' + '.xml', {"user":user, "latest":latest}, mimetype="application/xml")
    else:
        return render_to_response('main' + '.html', {"user":user, "latest":latest})

@csrf_exempt
def mobilelogin(request):
    print request
    vapp = app(request)

    if vapp:
	if request.method == "POST":
            try:
                    user = User.objects.get(username__exact = request.POST['id'])
                    # If the user already exists, tell user.
		    #user.social_auth.extra_data = '{"access_token": "'+ request.POST['access_token'] + '", "expires": "'+request.POST['expires']+'", "id": "'+request.POST['id']+'"}'
		    #user.save()
		    social_user = UserSocialAuth.objects.get(uid__exact = request.POST['id'])
		    social_user.extra_data = '{"access_token": "'+ request.POST['access_token'] + '", "expires": "'+request.POST['expires']+'", "id": "'+request.POST['id']+'"}'
		    social_user.save()

                    explan = 'You are loged in'
                    return render_to_response ('info' + '.xml', {"explan":explan}, mimetype="application/xml")
            except User.DoesNotExist:
		create_fb_user(request.POST['access_token'],request.POST['expires'],request.POST['id'],request.POST['firstName'],request.POST['lastName'])
		explan = 'User registered succesfully'
                return render_to_response ('info' + '.xml', {"explan":explan}, mimetype="application/xml")

    else:
        explan = 'This is not a mobile phone'
        return render_to_response('info.html', {"explan":explan})

	#return HttpResponseNotFound()

def canvas(request):

    latest = Post.objects.order_by('-date')[:30]


    if request.user.is_authenticated():
        user = request.user

    else:
        user = None

    return render_to_response('canvas' + '.html', {"user":user, "latest":latest})

def about(request):
    if request.user.is_authenticated():
        user = request.user

    else:
        user = None
    explan = 'This web is part of an assigment of <a href="https://noppa.aalto.fi/noppa/kurssi/t-110.5140/">Network Application Frameworks</a>'


    return render_to_response("info.html", {"explan":explan, "user":user})

@csrf_exempt
def location(request):
    print request
    vapp = app(request)
	    

    if request.user.is_authenticated():
        user = request.user
    else:
	user = None
    print user

    if vapp:
	user = checkuser(request.POST['access_token'],request.POST['id'])
    print user
 #   print "cuqueoo"
    if user != None:
    

        try:

            oauthToken = user.social_auth.get().extra_data
            userID = user.social_auth.get().extra_data['id']

            graph = facebook.GraphAPI(oauthToken['access_token'])
            #profile = graph.get_object(userID)
            #userlocation = profile["location"]


            friends = graph.get_connections(userID, "friends")
            dictFriends = {}
            for tmpUser in friends.get("data"):
                actFriendID = tmpUser.get("id")
                usersys = UserSocialAuth.objects.filter(uid = actFriendID)


                if (len(usersys) > 0) :
                    try:
                        friend =graph.get_object(actFriendID)
                        friendFBlocation = friend["location"]["name"]
                        
                        friendName = friend["name"]

                        
                        if(friendFBlocation in dictFriends):
                            dictFriends[friendFBlocation].append(friendName)
                        else:
                            tmpList = []
                            tmpList.append(friendName)
                            dictFriends[friendFBlocation] = tmpList

                        #print dictFriends
                    except:
                        print "Errorlocation!"

            #fp = graph.get_object('100003511321848')
            #userFBlocation = fb["location"]
            #print list(list(friends)
            #print userFBlocation #friends.get('data')

         #   print dictFriends

        
            if vapp:
#		print user.username
                return render_to_response ('location' + '.xml', {"user":user, "dictFriends":dictFriends}, mimetype="application/xml")
            else:
                return render_to_response("location.html", {"user":user, "dictFriends":dictFriends})
        except facebook.GraphAPIError:
            explan = 'The username '+userTarget+' is not on our system'

            if vapp:
                return render_to_response ('info' + '.xml', {"explan":explan, "user":user}, mimetype="application/xml")
            else:
                return render_to_response('info.html', {"explan":explan, "user":user})

    return HttpResponseNotFound('<h1>Page not found</h1>')

def showUser(request,userTarget):
    
    vapp = app(request)

    if request.user.is_authenticated():
        user = request.user

    else:
        user = None

    try:
        userT = User.objects.get(username=userTarget)

    except User.DoesNotExist:

        explan = 'The user ' + userTarget + ' is not on out system'

        if vapp:
            return render_to_response ('info' + '.xml', {"explan":explan, "user":user}, mimetype="application/xml")
        else:
            return render_to_response("info.html", {"explan":explan, "user":user})

    try:

        oauthToken = userT.social_auth.get().extra_data
        userID = userT.social_auth.get().extra_data['id']

        graph = facebook.GraphAPI(oauthToken['access_token'])
        #graph.get_connections(userT.username, "photos")
        profile = graph.get_object(userID)
        gender = profile["gender"]
        dateofbirth = profile["birthday"]
        userlocation = profile["location"]

        extrainfo = {
            "gender":gender,
            "dateofbirth":dateofbirth,
            "userlocation":userlocation,
            "picturelink":"http://graph.facebook.com/"+ userID +"/picture?type=large"
        }



        if vapp:
            return render_to_response ('user' + '.xml', {"user":user, "userT":userT, "extrainfo":extrainfo}, mimetype="application/xml")
        else:
            return render_to_response('user' + '.html', {"user":user, "userT":userT, "extrainfo":extrainfo})
    except facebook.GraphAPIError:
        explan = 'The username '+userTarget+' is not on our system'

        if vapp:
            return render_to_response ('info' + '.xml', {"explan":explan, "user":user}, mimetype="application/xml")
        else:
            return render_to_response('info.html', {"explan":explan, "user":user})

    return HttpResponseNotFound('<h1>Page not found</h1>')

@csrf_exempt
def createPost(request):
    """
    Creates a new brand post.

    @param request: HttpRequest object containing metadata about the request.
    @return: HttpResponse containing either the next step in the creation process or information
    messages.
    """
    vapp = app(request)
    # Getting other users in order to show them in the selector located at the
    # right side of the pobal name 'GraphAPIError' is not definedage.
 
    print request
    user = request.user
    if vapp:
	user = checkuser(request.POST['access_token'],request.POST['id'])

    # Only logged in users can create albums.
    if user:
        # GET request. Show the first step: album name and description.
        if request.method == "GET":
            return render_to_response('create.html', {"user":user})
        else:
            try:
                    Post.objects.get(owner = user, title = request.POST['title'].replace(" ", "_"))
                    # If the post already exists, tell user.
                    explan = 'An post with this name ("' + request.POST['title']
                    explan +=  '") already exists.<a href="/post/create" title="Create new post"> Please,Try again</a>'
                    if vapp:
            		return render_to_response ('info' + '.xml', {"explan":explan, "user":user}, mimetype="application/xml")
        	    else:
			return render_to_response("info.html", {"explan":explan, "user":user})
            except Post.DoesNotExist:
                    # Post name in the database is stored replacing blanks by "_"
                    title = request.POST['title'].replace(" ", "_")
                    post = Post(title=title,
                            description=request.POST['description'],
                            owner = user.username,
                            date = datetime.now(),
                            link = request.POST['link']
                            );
                    post.save();
		    if vapp:
            		latest = [post]
            		return render_to_response ('main' + '.xml', {"user":user, "latest":latest}, mimetype="application/xml")
                    explan = 'Your post <a href=/post/'+user.username+'/'+title+' >' + request.POST['title']
                    explan +=  '</a> has been published'

                    return render_to_response('info.html', {"explan":explan, "user":user})
                
    
def showUserPosts(request,userTarget):

    vapp = app(request)

    posts = Post.objects.filter(owner = userTarget).order_by('-date')[:30]

    if request.user.is_authenticated():
        user = request.user
    else:
        user = None


    print "Im showing userposts"
    print userTarget
    if vapp:
        return render_to_response ('main' + '.xml', {"user":user, "latest":posts}, mimetype="application/xml")
    else:
        return render_to_response('userPosts.html', {"user":user, "posts":posts})

def showPost(request,userTarget,postName):

    vapp = app(request)

    if request.user.is_authenticated():
        user = request.user
    else:
        user = None

    if request.method == "GET":
        try:
            post = Post.objects.get(owner__exact=userTarget, title__exact=postName)
        except Post.DoesNotExist:
            explan = "The post you are trying to view (" + postName +") does not exist"
            explan += '<a href="/">Go to main page</a>'
            if vapp:
                return render_to_response ('info' + '.xml', {"explan":explan, "user":user}, mimetype="application/xml")
            else:
                return render_to_response('info.html', {"explan":explan, "user":user})
        print "im showing showposts"
        if vapp:
            latest = [post]
            return render_to_response ('main' + '.xml', {"user":user, "latest":latest}, mimetype="application/xml")
        else:
            return render_to_response('showPost.html', {"user":user, "post":post})
    else:
        try:
            post = Post.objects.get(owner__exact=userTarget, title__exact=postName)

        except Post.DoesNotExist:
            explan = "The post you are trying to view (" + postName +") does not exist"
            explan += '<a href="/">Go to main page</a>'
            if vapp:
                return render_to_response ('info' + '.xml', {"explan":explan, "user":user}, mimetype="application/xml")
            else:
                return render_to_response('info.html', {"explan":explan, "user":user})



        oauthToken = user.social_auth.get().extra_data
        graph = facebook.GraphAPI(oauthToken['access_token'])
    

        postDict =     {"name": post.title,
             "link": host+"/post/"+post.owner + "/" + post.title,
             "caption": "",
             "description": post.description,
             "picture": post.link}
       
   
        graph.put_object("me", "feed", message=post.description, **postDict)
        explan = "The post (" + postName +") has been published into your wall."
        explan += '<a href="/">Go to main page</a>'
        return render_to_response('info.html', {"explan":explan, "user":user})
