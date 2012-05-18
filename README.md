LocalRecommendation
===================

Social network for local recommendation such as restaurants

GET THE REPO:
git clone git@github.com:scribbler87/LocalRecommendation.git


HOW TO START THE LOCAL DJANGO SERVER:
cd $YOUR_PAHT/LocalRecommendation/application/server/deepint
python manage.py runserver IP_ADDRESS:PORT

for the ipadress you can use the inet addr which you get from ifconfig

HOW TO SET UP ECLIPSE:
create an emulator with google api 1o and add the GPS device
Import--> import project from existing sourcecode:
        - $YOUR_PATH/LocalRecommendation/application
        - $YOUR_PATH/LocalRecommendation/facebook

Then right click on the application project--> properties--> android
--> add the facebook project as a library


START AND PLAY WITH THE APPLICATION:
In this project you can find examples for:
- facebook connect
- server side usage of graph api
- basics of django
- a RESTful server
- tab view
- example of googlemaps in android
- ...
