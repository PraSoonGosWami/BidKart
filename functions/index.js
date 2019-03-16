const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


// motifies seller for new bid
exports.pushNotification = functions.database.ref('/product/{pId}').onWrite( event => {

    console.log('Push notification event triggered');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.after.val();
    var sellerUID = valueObject.sellerUID;
    var befOb = event.before.val();
    var bBidder = befOb.bidderUID;
    var aBidder = valueObject.bidderUID;
    
    if(!(bBidder.localeCompare(aBidder))){
        console.log('Push notification not be sent');

    }
    else{
        return admin.database().ref('UserProfile/'+sellerUID+'/uToken').once('value').then((snap) => {
            const other = snap.val();
            // Create a notification
            const payload = {
                notification: {
                    title:valueObject.pName,
                    body: "Your product has a new bid",
                    sound: "default"
                
                },
            };

            return admin.messaging().sendToDevice(other,payload);
        });
    }
    return 0;
   
});

//notifies seller of approval
exports.LiveProducts = functions.database.ref('/product/{pId}').onUpdate( event => {

    console.log('Product Live event triggered');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.after.val();
    var status = valueObject.pStatus;
    var sellerUID = valueObject.sellerUID;
    var befOb = event.before.val();
    var bStatus= befOb.pStatus;
    var aStatus = valueObject.pStatus;
    
    if(!(status.localeCompare("live"))){
        if(!(bStatus.localeCompare(aStatus))){
            console.log('Approval not be sent');
    
        }
        else{
            return admin.database().ref('UserProfile/'+sellerUID+'/uToken').once('value').then((snap) => {
                const token = snap.val();
                // Create a notification
                const payload = {
                    notification: {
                        title:valueObject.pName+" is now LIVE",
                        body: "Your product has been aproved by the admin\nHappy Bidding!!",
                        sound: "default"
                    
                    },
                };

                return admin.messaging().sendToDevice(token,payload);
            });
        }
        
    }
    return 0;
   
});

//notifies buyer when status == sold
exports.SoldProduct = functions.database.ref('/product/{pId}').onUpdate( event => {

    console.log('Product Sold buyer notify');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.after.val();
    var status = valueObject.pStatus;
    var bidder = valueObject.bidderUID;
    var sellerUID = valueObject.sellerUID;
    var befOb = event.before.val();
    var bStatus= befOb.pStatus;
    var aStatus = valueObject.pStatus;

    if(!(status.localeCompare("sold"))){
        if(!(bStatus.localeCompare(aStatus))){
            console.log('Approval not be sent');
    
        }
        else{
            return admin.database().ref('UserProfile/'+bidder+'/uToken').once('value').then((snap) => {
                const token = snap.val();
                // Create a notification
                const payload = {
                    notification: {
                        title:valueObject.pName+" is now all yours",
                        body: "Complete the payment to get your product at your door step :-)",
                        sound: "default"
                    
                    },
                };

                return admin.messaging().sendToDevice(token,payload);
            });
        }
        
    }
    return 0;
   
});

//notifies seller when status == sold
exports.SoldProductNoti = functions.database.ref('/product/{pId}').onUpdate( event => {

    console.log('Product sold seller notify');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.after.val();
    var status = valueObject.pStatus;
    var sellerUID = valueObject.sellerUID;
    var befOb = event.before.val();
    var bStatus= befOb.pStatus;
    var aStatus = valueObject.pStatus;

    if(!(status.localeCompare("sold"))){
        if(!(bStatus.localeCompare(aStatus))){
            console.log('Approval not be sent');
    
        }
        else{
            return admin.database().ref('UserProfile/'+sellerUID+'/uToken').once('value').then((snap) => {
                const token = snap.val();
                // Create a notification
                const payload = {
                    notification: {
                        title:valueObject.pName+" is sold",
                        body: "Get the complete details here",
                        sound: "default"
                    
                    },
                };

                return admin.messaging().sendToDevice(token,payload);
            });
        }
        
    }
    return 0;
   
});