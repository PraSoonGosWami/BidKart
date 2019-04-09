const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.pushNotification = functions.database.ref('/product/{pId}').onUpdate( event => {

    console.log('Push notification event triggered');

    //  Grab the current value of what was written to the Realtime Database.
    var valueObject = event.after.val();
    var sellerUID = valueObject.sellerUID;
    var befOb = event.before.val();
    var bBidder = befOb.bidderUID;
    var aBidder = valueObject.bidderUID;
    var bStatus= befOb.pStatus;
    var aStatus = valueObject.pStatus;

    

    //sends notification for new bids to seller
    if( (bBidder.localeCompare(aBidder))!==0 )
    {
        return admin.database().ref('UserProfile/'+sellerUID+'/uToken').once('value').then((snap) => {
            const other = snap.val();
            // Create a notification
            const payload = {
                
                data: {
                    pid:valueObject.pId,
                    url:valueObject.productListImgURL,
                    title:valueObject.pName,
                    body: "Your product has a new bid",
                    sound: "default",
                    click_action: "OPEN_PRODUCT_ACTIVITY",
                    tag: valueObject.pId
                }
            };

            return admin.messaging().sendToDevice(other,payload);
        });
    }


    //sends notification to seller of approval of product
    if(!(aStatus.localeCompare("live"))){
        if( (bStatus.localeCompare(aStatus))!==0 ) {
        
            return admin.database().ref('UserProfile/'+sellerUID+'/uToken').once('value').then((snap) => {
                const token = snap.val();
                // Create a notification
                const payload = {
                
                    data: {
                        pid:valueObject.pId,
                        url:valueObject.productListImgURL,
                        title:valueObject.pName+" is now LIVE",
                        body: "Your product has been aproved by the admin\nHappy Bidding!!",
                        sound: "default",
                        click_action: "OPEN_PRODUCT_ACTIVITY",
                        tag: valueObject.pId
                    }
                };

                return admin.messaging().sendToDevice(token,payload);
            });
        }
        
    }



    //notifies buyer when status == sold
    if(!(aStatus.localeCompare("sold"))){
        if( (bStatus.localeCompare(aStatus)) !==0 ){
            return admin.database().ref('UserProfile/'+aBidder+'/uToken').once('value').then((snap) => {
                const token = snap.val();
                // Create a notification
                const payload = {
            
                    data: {
                        pid:valueObject.pId,
                        url:valueObject.productListImgURL,
                        title:valueObject.pName+" is now all yours",
                        body: "Complete the payment to get your product at your door step :-)",
                        sound: "default",
                        click_action: "OPEN_PRODUCT_ACTIVITY",
                        tag: valueObject.pId
                    }
                };
                return admin.messaging().sendToDevice(token,payload);
            })

            ,admin.database().ref('UserProfile/'+sellerUID+'/uToken').once('value').then((snap) => {
                const token = snap.val();
                // Create a notification
                const payload = {
                    
                    data: {
                        pid:valueObject.pId,
                        url:valueObject.productListImgURL,
                        title:valueObject.pName+" is sold",
                        body: "Get the complete details here",
                        sound: "default",
                        click_action: "OPEN_PRODUCT_ACTIVITY",
                        tag: valueObject.pId
                    }
                };

                return admin.messaging().sendToDevice(token,payload);
            });
            
        }
        
    }

return 0;
   
});