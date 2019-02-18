const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


// Listens for new messages added to messages/:pushId
exports.pushNotification = functions.database.ref('/product/{pId}').onWrite( event => {

    console.log('Push notification event triggered');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.after.val();



  // Create a notification
    const payload = {
        notification: {
            title:valueObject.pName+" is on",
            body: "Start Bidding Now",
            sound: "default"
        },
    };

    
    return admin.messaging().sendToDevice(valueObject.uToken,payload);

});
//dQFcHCPsAD0:APA91bHVtMjm0ZEE1kD0OGpuicJA5tRllEGiXicAFu0nXKPQ-kys-JYLe6I8ZVWUeZmFt54GMEvQCAuKg0I6gEmyZk2kqk8KipoZCYyWIlU8YtL_mDfhhQRRdQBveUV9xQbT35w_1CsE