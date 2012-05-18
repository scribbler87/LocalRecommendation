from django.db import models

# Create your models here.

class Post(models.Model):
    title        = models.CharField(max_length = 64);
    description = models.TextField();
    owner       = models.CharField(max_length = 64);
    date        = models.DateTimeField();
    link        =  models.URLField(max_length = 256);

    def settitle(self, title):
        if (title != None):
            self.title = title;
            self.save();

    def setDescription(self, descr):
        if (descr != None):
            self.description = descr;
            self.save();

    def setLink(self, link):
        if (link != None):
            self.link = link;
            self.save();