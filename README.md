# Meetup Liferay Event :
Modules presentation : 

1- demo-portlet : module Portlet MVC, export excel file with too ways : 
  * Synchronous way: direct call of ressource URL, the file will be send by the server after treatement finish.
  * Asynchronous way: ajax call of the server, asynchrone thread via message bus Liferay will process the file. The user will be notified when the treatement is finish.The file can be downloaded after. 

2- my-message-bus-destination: module to declare destination of message bus Liferay 

3- my-message-bus-listener: listener to trigg the event of send message to the destination, in this module we will generate the file in document library and all the informations will be send to notification in order to be display to user.

4-user-custom-notification: module to configure the custum message of the notification
